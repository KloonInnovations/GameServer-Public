package io.kloon.gameserver.modes.creative;

import com.spotify.futures.CompletableFutures;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.KloonGameServer;
import io.kloon.gameserver.commands.player.SettingsCommand;
import io.kloon.gameserver.creative.storage.CreativeChunkLoader;
import io.kloon.gameserver.creative.storage.CreativeWorldsRepo;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.saves.WorldSave;
import io.kloon.gameserver.creative.storage.saves.WorldSaveRepo;
import io.kloon.gameserver.creative.storage.saves.WorldSaveWithData;
import io.kloon.gameserver.minestom.events.AnnotationEventsRegisterer;
import io.kloon.gameserver.modes.GameServerMode;
import io.kloon.gameserver.modes.ModeType;
import io.kloon.gameserver.modes.creative.commands.*;
import io.kloon.gameserver.modes.creative.commands.history.HistoryCommand;
import io.kloon.gameserver.modes.creative.commands.history.RedoCommand;
import io.kloon.gameserver.modes.creative.commands.history.UndoCommand;
import io.kloon.gameserver.modes.creative.commands.enderchest.EnderChestCommand;
import io.kloon.gameserver.modes.creative.commands.masks.ClearMasksCommand;
import io.kloon.gameserver.modes.creative.commands.masks.MaskCommand;
import io.kloon.gameserver.modes.creative.commands.masks.UndressCommand;
import io.kloon.gameserver.modes.creative.commands.menus.WorldAdminCommand;
import io.kloon.gameserver.modes.creative.commands.preferences.FlySpeedCommand;
import io.kloon.gameserver.modes.creative.commands.jobs.CancelJobCommand;
import io.kloon.gameserver.modes.creative.commands.jobs.JobsCommand;
import io.kloon.gameserver.modes.creative.commands.preferences.SpeedEffectCommand;
import io.kloon.gameserver.modes.creative.commands.preferences.WalkSpeedCommand;
import io.kloon.gameserver.modes.creative.commands.snipe.SnipeCommand;
import io.kloon.gameserver.modes.creative.commands.test.*;
import io.kloon.gameserver.modes.creative.commands.test.schematic.ImportSchematicCommand;
import io.kloon.gameserver.modes.creative.commands.test.schematic.PaletteUpgradeCommand;
import io.kloon.gameserver.modes.creative.commands.tools.ClipboardCommand;
import io.kloon.gameserver.modes.creative.commands.tools.MenuCommand;
import io.kloon.gameserver.modes.creative.commands.tools.ToolCommand;
import io.kloon.gameserver.modes.creative.commands.tools.WaypointsCommand;
import io.kloon.gameserver.modes.creative.buildpermits.BuildPermitListener;
import io.kloon.gameserver.modes.creative.commands.world.TimeCommand;
import io.kloon.gameserver.modes.creative.commands.world.WeatherCommand;
import io.kloon.gameserver.modes.creative.menu.CreativeSettingsCommandMenu;
import io.kloon.gameserver.modes.creative.menu.CreativeMainMenu;
import io.kloon.gameserver.modes.creative.menu.SwapListener;
import io.kloon.gameserver.modes.creative.network.CreativeService;
import io.kloon.gameserver.modes.creative.storage.datainworld.CreativeWorldStorage;
import io.kloon.gameserver.modes.creative.storage.enderchest.EnderChestRepo;
import io.kloon.gameserver.modes.creative.storage.enderchest.EnderChestStorage;
import io.kloon.gameserver.modes.creative.storage.playerdata.CreativePlayerRepo;
import io.kloon.gameserver.modes.creative.storage.playerdata.CreativePlayerStorage;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.ToolsListener;
import io.kloon.gameserver.modes.creative.tools.impl.history.HistoryTool;
import io.kloon.gameserver.modes.creative.tools.impl.MainMenuTool;
import io.kloon.gameserver.modes.creative.tools.impl.waypoint.WaypointsTool;
import io.kloon.gameserver.modes.creative.tools.security.ToolSignature;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.KloonNetworkInfra;
import net.kyori.adventure.nbt.TagStringIO;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.entity.GameMode;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.item.ItemStack;
import net.minestom.server.listener.CreativeInventoryActionListener;
import net.minestom.server.listener.PlayerDiggingListener;
import net.minestom.server.listener.manager.PacketListenerManager;
import net.minestom.server.network.packet.client.play.ClientCreativeInventoryActionPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CreativeMode extends GameServerMode {
    private static final Logger LOG = LoggerFactory.getLogger(CreativeMode.class);

    public static final String SELECTION_ICON = "⿻";
    public static final String SNIPE_ICON = "⌖";

    private final CreativeWorldsRepo worldsRepo;
    private final CreativePlayerRepo playersRepo;
    private final EnderChestRepo enderChestRepo;
    private final ToolsListener toolsListener;

    public CreativeMode(KloonNetworkInfra infra) {
        super(infra);
        this.worldsRepo = new CreativeWorldsRepo(infra);
        this.playersRepo = new CreativePlayerRepo(infra.mongo());
        this.enderChestRepo = new EnderChestRepo(infra.mongo());

        this.toolsListener = new ToolsListener();
    }

    public CreativeWorldsRepo getWorldsRepo() {
        return worldsRepo;
    }

    public CreativePlayerRepo getPlayersRepo() {
        return playersRepo;
    }

    public EnderChestRepo getEnderChestRepo() {
        return enderChestRepo;
    }

    public ToolsListener getToolsListener() {
        return toolsListener;
    }

    public WaypointsTool getWaypointTool() {
        return (WaypointsTool) toolsListener.get(CreativeToolType.WAYPOINTS);
    }

    public HistoryTool getHistoryTool() {
        return (HistoryTool) toolsListener.get(CreativeToolType.HISTORY);
    }

    @Override
    public void onStart(KloonGameServer kgs) {
        MinecraftServer.getConnectionManager().setPlayerProvider(CreativePlayer::new);

        kgs.getServiceBox().register(new CreativeService(kgs, this));

        AnnotationEventsRegisterer events = new AnnotationEventsRegisterer();
        events.register(toolsListener);
        events.register(new SwapListener(toolsListener));
        events.register(new BuildPermitListener());

        CommandManager commandMan = MinecraftServer.getCommandManager();

        MainMenuTool mainMenuTool = new MainMenuTool();
        toolsListener.register(mainMenuTool);
        for (CreativeToolType toolType : CreativeToolType.values()) {
            CreativeTool<?, ?> tool = toolType.instantiate();
            if (tool == null) continue;
            toolsListener.register(tool);
            tool.createCommands().forEach(commandMan::register);
        }

        commandMan.register(new MenuCommand(mainMenuTool));
        commandMan.register(new ToolCommand());
        commandMan.register(new ClearCommand());
        commandMan.register(new ClearMasksCommand());
        commandMan.register(new DropCommand());
        commandMan.register(new RespawnCommand());
        commandMan.register(new WeatherCommand());;
        commandMan.register(new TimeCommand());
        commandMan.register(new FlySpeedCommand());
        commandMan.register(new WalkSpeedCommand());
        commandMan.register(new SpeedEffectCommand());
        commandMan.register(new BackToHubCommand());
        commandMan.register(new WorldBorderCommand());
        commandMan.register(new NightVisionCommand());
        commandMan.register(new SnipeCommand());
        commandMan.register(new WorldAdminCommand());
        commandMan.register(new EnderChestCommand());
        commandMan.register(new UndressCommand());
        commandMan.register(new MaskCommand());

        commandMan.register(new ClearAuditHistoryCommand());
        commandMan.register(new WorldIdCommand());
        commandMan.register(new WorldBorderTestCommand());
        commandMan.register(new WriteStuffCommand());
        commandMan.register(new MyHeadCommand());
        commandMan.register(new TestSwirlCommand());
        commandMan.register(new DimensionInfoCommand());
        commandMan.register(new CreateWorldLinkCommand());
        commandMan.register(new ImportSchematicCommand());
        commandMan.register(new PaletteUpgradeCommand());

        commandMan.register(new SaveCreativeWorldCommand());
        commandMan.register(new JobsCommand());
        commandMan.register(new CancelJobCommand());
        commandMan.register(new WaypointsCommand());
        commandMan.register(new ClipboardCommand());
        commandMan.register(new UndoCommand());
        commandMan.register(new RedoCommand());
        commandMan.register(new HistoryCommand());
        commandMan.register(new BuildPermitsCommand());
        commandMan.register(new OldSaveDoSaveCommand());

        commandMan.register(new SettingsCommand() {
            @Override
            public void openMenu(KloonPlayer player) {
                CreativeMainMenu mainMenu = new CreativeMainMenu((CreativePlayer) player);
                new CreativeSettingsCommandMenu(mainMenu).display(player);
            }
        });

        kgs.getPlayerLoadListener().addLoader((player, account) -> {
            CreativePlayer creativePlayer = (CreativePlayer) player;
            CompletableFuture<CreativePlayerStorage> loadCreativeStorage = playersRepo.get(account).thenApply(storage -> {
                creativePlayer.setCreativeStorage(storage);
                return storage;
            });
            CompletableFuture<EnderChestStorage> loadEnderChest = loadCreativeStorage
                    .thenCompose(_ -> enderChestRepo.getItems(creativePlayer))
                    .thenApply(items -> {
                        EnderChestStorage storage = new EnderChestStorage(creativePlayer, items);
                        creativePlayer.setEnderChest(storage);
                        return storage;
                    });
            return CompletableFuture.allOf(loadCreativeStorage, loadEnderChest);
        });

        kgs.getScheduler().buildShutdownTask(() -> {
            InstanceManager instanceMan = kgs.getInstanceManager();
            List<CompletableFuture<Void>> saveFutures = new ArrayList<>();
            instanceMan.getInstances().forEach(instance -> {
                if (!(instance instanceof CreativeInstance creativeInstance)) {
                    return;
                }
                if (creativeInstance.isOldSaveDontSave()) {
                    return;
                }
                saveFutures.add(creativeInstance.saveInstance(WorldSave.Reason.SERVER_CLOSE));
            });
            try {
                CompletableFutures.allAsList(saveFutures).get(4, TimeUnit.SECONDS);
            } catch (Throwable t) {
                LOG.error("Error during shutdown hook instances closures", t);
            }
        });

        PacketListenerManager packetMan = MinecraftServer.getPacketListenerManager();
        packetMan.setPlayListener(ClientCreativeInventoryActionPacket.class, (packet, p) -> {
            if (!(p instanceof CreativePlayer player)) {
                CreativeInventoryActionListener.listener(packet, p);
                return;
            }

            if (player.getGameMode() != GameMode.CREATIVE) {
                return;
            }

            ItemStack item = packet.item();
            ToolSignature.Validity validity = ToolSignature.isValid(item);
            if (!validity.isValid()) {
                LOG.info(STR."\{player} sent invalid item from creative \{validity} \{item}");
                //LOG.info(STR."received item nbt = \{item.toItemNBT().toString()}");
                try {
                    LOG.info(STR."received item nbt = \{TagStringIO.tagStringIO().asString(item.toItemNBT())}");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                p.kick(MM."<red>Client sent invalid item.");
                return;
            }
            if (validity == ToolSignature.Validity.VALID_CONFIRMED) {
                item = item.withAmount(1);
                packet = new ClientCreativeInventoryActionPacket(packet.slot(), item);
            }

            CreativeInventoryActionListener.listener(packet, p);
        });

        packetMan.setPlayListener(ClientPlayerDiggingPacket.class, (packet, p) -> {
            if (!(p instanceof CreativePlayer player)) {
                PlayerDiggingListener.playerDiggingListener(packet, p);
                return;
            }

            boolean handled = player.getSnipe().handleDigPacket(packet);
            if (!handled) {
                PlayerDiggingListener.playerDiggingListener(packet, p);
            }
        });
    }

    public boolean isTool(ItemStack item) {
        return item.hasTag(CreativeTool.TOOL_TYPE_TAG);
    }

    public boolean isTool(ItemStack item, CreativeToolType toolType) {
        String typeDbKey = item.getTag(CreativeTool.TOOL_TYPE_TAG);
        return toolType.getDbKey().equals(typeDbKey);
    }

    public CompletableFuture<CreativeInstance> createInstanceLatestSave(ObjectId worldId) {
        WorldSaveRepo savesRepo = worldsRepo.saves();

        CompletableFuture<WorldDef> getWorldDef = worldsRepo.defs().getWorldDef(worldId);
        CompletableFuture<WorldSaveWithData> getLatestSave = getWorldDef
                .thenCompose(savesRepo::getLatestSave)
                .thenCompose(savesRepo::getSaveData);
        return createInstance(getWorldDef, getLatestSave);
    }

    public CompletableFuture<CreativeInstance> createInstanceSpecificSave(ObjectId worldId, ObjectId saveId) {
        WorldSaveRepo savesRepo = worldsRepo.saves();

        CompletableFuture<WorldDef> getWorldDef = worldsRepo.defs().getWorldDef(worldId);
        CompletableFuture<WorldSaveWithData> getSave = savesRepo.getWorldSave(saveId)
                .thenCompose(savesRepo::getSaveData)
                .thenApply(data -> {
                    if (data == null) throw new RuntimeException(STR."Save data is null on specific-save instance request \{saveId.toHexString()}");
                    return data;
                });

        CompletableFuture<CreativeInstance> createInstance = createInstance(getWorldDef, getSave);
        createInstance.thenApply(instance -> instance.withOldSaveDontSave(true));
        return createInstance;
    }

    private CompletableFuture<CreativeInstance> createInstance(CompletableFuture<WorldDef> getWorldDef, CompletableFuture<WorldSaveWithData> getSave) {
        CompletableFuture<CreativeWorldStorage> getCustomData = getSave.thenApply(this::deserializeCustomData);
        return CompletableFuture.allOf(getWorldDef, getSave, getCustomData).thenApplyAsync(_ -> {
            WorldDef worldDef = getWorldDef.join();
            if (worldDef.datacenter() != infra.datacenter()) {
                throw new RuntimeException(STR."Attempted creating world from \{worldDef.datacenter()} on \{infra.datacenter()} instance");
            }

            return createInstance(worldDef, getSave.join(), getCustomData.join());
        }, MinecraftServer.getSchedulerManager());
    }

    private CreativeWorldStorage deserializeCustomData(WorldSaveWithData data) {
        byte[] bytes = data == null ? null : data.customBytes();
        if (bytes == null) return new CreativeWorldStorage();
        try {
            return CreativeWorldStorage.MSG_PACK.readValue(bytes, CreativeWorldStorage.class);
        } catch (Throwable t) {
            throw new RuntimeException("Error deserializing instance custom data", t);
        }
    }

    private CreativeInstance createInstance(WorldDef worldDef, @Nullable WorldSaveWithData worldSave, CreativeWorldStorage storage) {
        InstanceManager instanceMan = MinecraftServer.getInstanceManager();

        WorldSave seedingSave = worldSave == null ? null : worldSave.worldSave();
        byte[] polarBytes = worldSave == null ? null : worldSave.polarBytes();

        if (polarBytes == null) {
            LOG.info(STR."Creating world without saving save seeding=\{seedingSave == null ? "null" : seedingSave.hexId()}");
        }

        UUID instanceId = UUID.randomUUID();
        CreativeChunkLoader chunkLoader = new CreativeChunkLoader(worldDef, seedingSave, polarBytes, storage, instanceId, worldsRepo.saves());
        CreativeInstance instance = new CreativeInstance(instanceId, this, chunkLoader);
        instance.setChunkSupplier(LightingChunk::new);
        instanceMan.registerInstance(instance);

        ObjectId worldId = worldDef._id();
        Kgs.getCreative().worldInvalidateSub(instance.scheduler(), worldId, () -> !instance.isClosed(), _ -> {
            worldsRepo.defs().getWorldDef(worldId).thenAcceptAsync(newDef -> {
                WorldDef before = chunkLoader.getWorldDef();
                if (newDef == null) return;
                chunkLoader.setWorldDef(newDef);
                instance.onWorldDefInvalidate(before, newDef);
            }, instance.scheduler());
        });

        instance.initialize();

        return instance;
    }

    @Override
    public ModeType getType() {
        return ModeType.CREATIVE;
    }
}
