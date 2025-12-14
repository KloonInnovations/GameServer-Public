package io.kloon.gameserver.modes.creative;

import com.google.common.base.Strings;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.allocation.AllocateSlotReply;
import io.kloon.gameserver.allocation.AllocateSlotRequest;
import io.kloon.gameserver.creative.storage.CreativeChunkLoader;
import io.kloon.gameserver.creative.storage.defs.BuildPermit;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.owner.WorldOwner;
import io.kloon.gameserver.creative.storage.owner.state.ErrorWorldOwner;
import io.kloon.gameserver.creative.storage.owner.state.LoadingWorldOwner;
import io.kloon.gameserver.creative.storage.saves.WorldSave;
import io.kloon.gameserver.minestom.KloonInstance;
import io.kloon.gameserver.minestom.blocks.KloonPlacementRules;
import io.kloon.gameserver.minestom.blocks.vanilla.interactions.CustomBlockInteractEvent;
import io.kloon.gameserver.minestom.components.ComponentWrapper;
import io.kloon.gameserver.minestom.events.AnnotationEventsRegisterer;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.blockedits.byhand.CreativeBlockPlacedByHandEvent;
import io.kloon.gameserver.modes.creative.commands.OldSaveDoSaveCommand;
import io.kloon.gameserver.modes.creative.history.audit.AuditRecord;
import io.kloon.gameserver.modes.creative.jobs.BlocksJob;
import io.kloon.gameserver.modes.creative.jobs.BlocksJobQueue;
import io.kloon.gameserver.modes.creative.blockedits.authorization.BlockEditDenial;
import io.kloon.gameserver.modes.creative.blockedits.authorization.BlockEditAuthorizationEvent;
import io.kloon.gameserver.modes.creative.blockedits.PlayerBlockEditListener;
import io.kloon.gameserver.modes.creative.masks.MasksListener;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.blocks.PatternBlock;
import io.kloon.gameserver.modes.creative.storage.datainworld.CreativeWorldStorage;
import io.kloon.gameserver.modes.creative.storage.datainworld.PlayerWorldStorage;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointStorage;
import io.kloon.gameserver.modes.creative.storage.datainworld.world.CreativeTimeStorage;
import io.kloon.gameserver.modes.creative.tasks.CloseInstanceWhenReady;
import io.kloon.gameserver.modes.creative.tasks.SaveOnceInAWhile;
import io.kloon.gameserver.modes.creative.tasks.TeleportPlayersWhoAreTooFar;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import io.kloon.gameserver.modes.creative.ux.CreativeBossBarTask;
import io.kloon.gameserver.modes.creative.ux.messaging.CreativeBroadcaster;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.service.allocations.SlotAllocationEvent;
import io.kloon.gameserver.util.input.InputFmt;
import io.kloon.infra.facts.KloonEnvironment;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CreativeInstance extends KloonInstance {
    private static final Logger LOG = LoggerFactory.getLogger(CreativeInstance.class);

    private final CreativeMode creative;
    private final CreativeChunkLoader chunkLoader;
    private final BlocksJobQueue blocksJobQueue;
    private final Map<UUID, BuildPermit> ephemeralPermits = new HashMap<>();

    private final CreativeBossBarTask bossBarTask;
    private final CreativeBroadcaster broadcaster;

    private WorldOwner.Loaded owner = new LoadingWorldOwner();

    private CompletableFuture<Void> closingTask = null;
    private boolean unregisterMe = false;
    private boolean oldSaveDontSave = false;

    private double timeDecimals = 0.0;

    public static final int MINUTES_BEFORE_AUTO_SAVE = 5;

    public CreativeInstance(UUID instanceId, CreativeMode creative, CreativeChunkLoader chunkLoader) {
        super(instanceId, DimensionType.OVERWORLD, chunkLoader);
        this.creative = creative;
        this.chunkLoader = chunkLoader;
        this.blocksJobQueue = new BlocksJobQueue(this, 25);
        this.bossBarTask = new CreativeBossBarTask(this, blocksJobQueue);
        this.broadcaster = new CreativeBroadcaster(this);
    }

    public WorldDef getWorldDef() {
        return chunkLoader.getWorldDef();
    }

    @Override
    public CreativeChunkLoader getChunkLoader() {
        return chunkLoader;
    }

    public void initialize() {
        if (!chunkLoader.hasSeedSave()) {
            setGenerator(unit -> {
                int chunkX = unit.absoluteStart().chunkX();
                int chunkZ = unit.absoluteStart().chunkZ();
                if (chunkX == 625 && chunkZ == 625) {
                    unit.modifier().fillHeight(30, 40, Block.DIRT);
                }
            });
        }

        CreativeWorldStorage worldStorage = getWorldStorage();

        setTime(worldStorage.getTime().getTimeLong());
        setTimeRate(0);

        setWeather(worldStorage.getWeather().asWeather());

        getWorldDef().owner().loadAsyncStuff().whenComplete((loaded, t) -> {
            if (t == null) {
                this.owner = loaded;
            } else {
                this.owner = new ErrorWorldOwner();
                LOG.error(STR."Error loading owner on instance boot \{getWorldDef().ownership()}", t);
            }
        });

        AnnotationEventsRegisterer registerer = new AnnotationEventsRegisterer(eventNode());
        registerer.register(new PlayerBlockEditListener());
        registerer.register(new MasksListener());

        eventNode().addListener(PlayerSpawnEvent.class, event -> {
            CreativePlayer player = (CreativePlayer) event.getPlayer();

            PlayerWorldStorage storage = getStorage(player);
            Pos spawnPos;
            if (player.canEditWorld()) {
                if (player.getCreativeStorage().isSpawningOnLastLocation()) {
                    Pos savedPosition = storage.getPosition();
                    spawnPos = savedPosition == null
                            ? getWorldSpawn()
                            : savedPosition;
                } else {
                    spawnPos = getWorldSpawn();
                }
            } else {
                spawnPos = getWorldSpawn();
            }

            player.setRespawnPoint(spawnPos);
            player.teleport(player.getRespawnPoint());

            player.setGameMode(GameMode.CREATIVE);

            if (oldSaveDontSave && player.canEditWorld()) {
                player.scheduleTicks(() -> {
                    player.playSound(SoundEvent.ITEM_GOAT_HORN_SOUND_7, Pitch.base(1.85).addRand(0.12), 0.63f);
                    player.sendMessage(MM."<#DB1578><st>\{Strings.repeat("-", 32)}");
                    player.sendMessage(ComponentWrapper.wrap(MM."<red>⏰ <b>READ:</b> <yellow>As the world loaded from a specific save, it <red>WILL NOT SAVE <yellow>until the <green>first auto-save<yellow>!", 50));
                    player.sendMessage(ComponentWrapper.wrap(MM."<aqua>First auto-save occurs in \{MINUTES_BEFORE_AUTO_SAVE} minutes!", 50));
                    player.sendMessage(MM."\{InputFmt.CLICK_GREEN} <dark_green>Click this <green>to enable saving now instead."
                            .hoverEvent(MM."<cta>Click to enable saving!")
                            .clickEvent(ClickEvent.runCommand("/" + OldSaveDoSaveCommand.LABEL)));
                    player.sendMessage(MM."<#DB1578><st>\{Strings.repeat("-", 32)}");
                }, 32);
            }

            player.onSpawn(this);
        });

        eventNode().addListener(SlotAllocationEvent.class, event -> {
            if (isClosed()) {
                event.setCancelled(true);
            }
        });

        eventNode().addListener(CustomBlockInteractEvent.class, event -> {
            CreativePlayer player = (CreativePlayer) event.getPlayer();
            if (!player.canEditWorld()) {
                event.setCancelled(true);
            }
        });

        eventNode().addListener(BlockEditAuthorizationEvent.class, event -> {
            CreativePlayer player = event.getPlayer();
            Point blockPos = event.getBlockPosition();
            if (!chunkLoader.isInBounds(blockPos)) {
                event.deny(BlockEditDenial.Source.WORLD_BORDERS, player::sendOutOfBoundsMessage);
                return;
            }
            BlocksJob job = blocksJobQueue.getJob(blockPos);
            if (job != null) {
                event.deny(BlockEditDenial.Source.PART_OF_JOB, () -> {
                    player.sendPit(NamedTextColor.RED, "NOPE", MM."<gray>This block is part of job: <white>\{job.getName()} <dark_gray>(\{job.getTicketNumber()})");
                });
            }
        });

        eventNode().addListener(BlockEditAuthorizationEvent.class, event -> {
            CreativePlayer player = event.getPlayer();
            BlockVec blockPos = event.getBlockPosition();
            BlocksJob job = blocksJobQueue.getJob(blockPos);
            if (job != null) {
                event.deny(BlockEditDenial.Source.PART_OF_JOB, () -> {
                    player.sendPit(NamedTextColor.RED, "NOPE", MM."<gray>This block is part of job: <white>\{job.getName()} <dark_gray>(\{job.getTicketNumber()})");
                });
            }
        });

        eventNode().addListener(SlotAllocationEvent.class, event -> {
            AllocateSlotRequest request = event.getRequest();
            if (getWorldDef().ownership().isOwner(request.accountId())) return;
            if (request.joining() != null && !worldStorage.canCommandJoin()) {
                event.setDeny(AllocateSlotReply.Status.JOIN_WORLD_IS_DISABLED, "/join world toggled off!");
            }
        });

        boolean devEnv = Kgs.getInfra().environment() == KloonEnvironment.DEV;

        Scheduler scheduler = scheduler();
        scheduler.scheduleTask(new SaveOnceInAWhile(this), TaskSchedule.minutes(MINUTES_BEFORE_AUTO_SAVE), TaskSchedule.minutes(10));
        scheduler.scheduleTask(new CloseInstanceWhenReady(this), TaskSchedule.seconds(devEnv ? 1 : 6), TaskSchedule.seconds(devEnv ? 1 : 5));
        scheduler.scheduleTask(new TeleportPlayersWhoAreTooFar(this), TaskSchedule.tick(1), TaskSchedule.tick(1));

        scheduler.scheduleNextTick(() -> {
            creative.getWaypointTool().spawnWaypoints(this);
        });

        scheduler.submitTask(() -> {
            if (isClosed()) return TaskSchedule.stop();
            try {
                blocksJobQueue.tick();
            } catch (Throwable t) {
                LOG.error("Error in blocks job queue", t);
            }
            return TaskSchedule.nextTick();
        }, ExecutionType.TICK_END);
    }

    public Pos getWorldSpawn() {
        WaypointStorage waypointSpawn = getWorldStorage().getWaypoints().getWorldSpawn();
        if (waypointSpawn != null) {
            return waypointSpawn.getPosition();
        }

        Pos worldCenter = getWorldStorage().getWorldCenter().toPos();
        return worldCenter.add(8, 0, 8).withY(41);
    }

    @Override
    public void tick(long time) {
        super.tick(time);
        bossBarTask.tick();
        tickTime();
        if (unregisterMe) {
            attemptUnregister();
        }
    }

    private void tickTime() {
        CreativeTimeStorage timeStorage = getWorldStorage().getTime();
        timeDecimals += timeStorage.getTimeRate();

        int asInt = (int) timeDecimals;
        if (asInt == 0) {
            timeStorage.setTime(getTime());
            return;
        }

        long time = getTime();
        time += asInt;

        timeDecimals -= asInt;

        if (time < 0) {
            time += 12_096_000; // a week irl
        }
        setTime(time + asInt);
        timeStorage.setTime(getTime());
    }

    @Override
    protected Block handleBlockPlacedByPlayer(BlockHandler.PlayerPlacement state, Block block) {
        CreativePlayer player = (CreativePlayer) state.getPlayer();
        BlockVec blockPos = new BlockVec(state.getBlockPosition());

        ItemStack inHand = player.getItemInHand(state.getHand());
        TinkeredBlock tinkered = TinkeredBlock.get(inHand);
        if (tinkered != null) {
            block = tinkered.block();
        }

        PatternBlock patternBlock = PatternBlock.get(inHand);
        if (patternBlock != null) {
            CreativePattern pattern = patternBlock.pattern();
            block = pattern.computeBlock(this, blockPos);
            if (block.isAir()) {
                player.msg().send(MsgCat.NEGATIVE, NamedTextColor.LIGHT_PURPLE, "OH?", MM."<gray>The pattern placed down air!",
                        SoundEvent.ENTITY_WIND_CHARGE_WIND_BURST, Pitch.rng(1.5, 0.3), 0.5);
            }
        }

        block = KloonPlacementRules.injectHandler(block);

        if (!block.isAir()) {
            EventDispatcher.call(new CreativeBlockPlacedByHandEvent(player, block, state.getBlockFace(), blockPos, state.getHand()));
        }
        return block;
    }

    public CreativeWorldStorage getWorldStorage() {
        return chunkLoader.getWorldStorage();
    }

    public PlayerWorldStorage getStorage(Player player) {
        return getWorldStorage().getPlayer(player.getUuid());
    }

    public BlocksJobQueue getJobQueue() {
        return blocksJobQueue;
    }

    public Map<UUID, BuildPermit> getEphemeralPermits() {
        return ephemeralPermits;
    }

    public WorldOwner.Loaded getOwner() {
        return owner;
    }

    public boolean isOutOfBounds(Point point) {
        return !isInBounds(point);
    }

    public boolean isOutOfBounds(BoundingBox boundingBox) {
        return isOutOfBounds(boundingBox.relativeStart()) || isOutOfBounds(boundingBox.relativeEnd());
    }

    public boolean isInBounds(Point point) {
        return chunkLoader.isInBounds(point);
    }

    public boolean isInBounds(BoundingBox boundingBox) {
        return isInBounds(boundingBox.relativeStart()) && isInBounds(boundingBox.relativeEnd());
    }

    public BlockVec getWorldCenter() {
        return getWorldStorage().getWorldCenter().toBlockVec();
    }

    public CreativeInstance withOldSaveDontSave(boolean oldSaveDontSave) {
        boolean before = this.oldSaveDontSave;
        this.oldSaveDontSave = oldSaveDontSave;
        if (before && !oldSaveDontSave) {
            streamPlayers().forEach(p -> {
                p.playSound(SoundEvent.BLOCK_BEEHIVE_ENTER, 0.5);
                p.sendPit(NamedTextColor.GREEN, "SAVING ON!", MM."<gray>Saving enabled for this older save!");
            });
        }
        return this;
    }

    public boolean isOldSaveDontSave() {
        return oldSaveDontSave;
    }

    public List<BuildPermit> getBuildPermits() {
        List<BuildPermit> permits = new ArrayList<>();
        permits.addAll(ephemeralPermits.values());
        permits.addAll(getWorldDef().buildPermits());
        permits.removeIf(p -> p.isExpired(this));
        return permits;
    }

    @Nullable
    public BuildPermit getPermitForPlayer(KloonPlayer player) {
        BuildPermit ephemeral = ephemeralPermits.get(player.getUuid());
        if (ephemeral != null) {
            if (ephemeral.isExpired(this)) {
                ephemeralPermits.remove(player.getUuid());
            } else {
                return ephemeral;
            }
        }
        BuildPermit persisted = getWorldDef().getPermitForPlayer(player.getAccountId());
        if (persisted != null && !persisted.isExpired(this)) {
            return persisted;
        }
        return null;
    }

    public void addToAuditHistory(AuditRecord record) {
        getWorldStorage().getAuditHistory().push(record);
    }

    public void onWorldDefInvalidate(WorldDef before, WorldDef after) {
        if (after.deleted()) {
            streamPlayers().forEach(p -> p.sendPit(NamedTextColor.RED, "GOODBYE!", MM."<gray>This world has been moved to the recycle bin!"));
            close();
        }
    }

    @Override
    public Stream<CreativePlayer> streamPlayers() {
        return getPlayers().stream().map(p -> (CreativePlayer) p);
    }

    @Override
    public @Nullable CreativePlayer getPlayerByUuid(UUID uuid) {
        return (CreativePlayer) super.getPlayerByUuid(uuid);
    }

    public CreativeBroadcaster broadcast() {
        return broadcaster;
    }

    public CompletableFuture<Void> saveInstance(WorldSave.Reason reason) {
        chunkLoader.setSaveReason(reason);
        return saveInstance();
    }

    @Override
    public @NotNull CompletableFuture<Void> saveInstance() {
        getPlayers().forEach(p -> {
            CreativePlayer player = (CreativePlayer) p;
            PlayerWorldStorage storage = getStorage(player);
            try {
                player.writeStuffToWorldStorage(storage);
                player.saveCreativeStorage();
            } catch (Throwable t) {
                LOG.error("Error writing player stuff", t);
            }
        });
        return super.saveInstance();
    }

    public void close() {
        if (isClosed()) {
            return;
        }

        LOG.info(STR."Closing instance \{getUniqueId()} with world \{getWorldDef()}...");
        streamPlayers().forEach(p -> p.kick(MM."<red>The instance is closing."));

        CompletableFuture<Void> saveOnClose;
        if (oldSaveDontSave) {
            saveOnClose = CompletableFuture.completedFuture(null);
        } else {
            saveOnClose = saveInstance(WorldSave.Reason.INSTANCE_CLOSE);
        }
        this.closingTask = CompletableFuture.allOf(saveOnClose).exceptionally(t -> {
            LOG.error("Error in instance closure", t);
            return null;
        }).thenRunAsync(() -> {
            unregisterMe = true;
            LOG.info("unregisterMe = true");
            attemptUnregister();
        }, scheduler()).exceptionally(t -> {
            LOG.error("Critical error while closing instance", t);
            return null;
        });
    }

    private void attemptUnregister() {
        try {
            if (!isRegistered()) {
                LOG.info("Attempted to unregister instance which isn't registered: " + getWorldDef());
                return;
            }
            InstanceManager instanceMan = MinecraftServer.getInstanceManager();
            instanceMan.unregisterInstance(this);
            LOG.info(STR."Unregistered instance \{getUniqueId()} with world \{getWorldDef()}...");
        } catch (Throwable t) {
            LOG.error(STR."Failed unregistered instance \{getUniqueId()} with world \{getWorldDef()}", t);
        }
    }

    public boolean isClosed() {
        return closingTask != null;
    }
}