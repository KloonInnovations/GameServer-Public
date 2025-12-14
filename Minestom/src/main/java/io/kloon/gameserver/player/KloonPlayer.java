package io.kloon.gameserver.player;

import io.kloon.bigbackend.transfers.TransferPlayer;
import io.kloon.bigbackend.transfers.TransferSlot;
import io.kloon.bigbackend.transfers.allocation.BackendAllocReply;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ChestMenuPlayer;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.minestom.InventoryExtras;
import io.kloon.gameserver.minestom.KloonInstance;
import io.kloon.gameserver.minestom.events.AnnotationEventsRegisterer;
import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.minestom.utils.ColorFmt;
import io.kloon.gameserver.player.proxyinfo.KloonProxyInfo;
import io.kloon.gameserver.player.settings.PlayerSettingsStorage;
import io.kloon.gameserver.player.settings.SettingsToggle;
import io.kloon.gameserver.service.TablistSync;
import io.kloon.gameserver.service.allocations.AllocatedSlot;
import io.kloon.gameserver.service.allocations.AllocationSlotsListener;
import io.kloon.gameserver.service.allocations.approved.ApprovedTransfer;
import io.kloon.gameserver.tablist.DefaultTablist;
import io.kloon.gameserver.tablist.KloonTablist;
import io.kloon.gameserver.tablist.VirtualTablist;
import io.kloon.gameserver.tablist.VirtualTablistListener;
import io.kloon.gameserver.util.coordinates.CardinalDirection;
import io.kloon.gameserver.util.physics.Ray;
import io.kloon.gameserver.ux.actionbar.ActionBarQueue;
import io.kloon.gameserver.ux.headerfooter.DefaultHeaderFooter;
import io.kloon.gameserver.ux.headerfooter.KloonHeaderFooter;
import io.kloon.gameserver.ux.sidebar.DefaultSidebar;
import io.kloon.gameserver.ux.sidebar.KloonSidebar;
import io.kloon.infra.mongo.accounts.KloonAccount;
import io.kloon.infra.mongo.accounts.projections.KloonMoniker;
import io.kloon.infra.ranks.PlayerRankCache;
import io.kloon.infra.ranks.RankLooks;
import io.kloon.velocity.mc.pluginchannel.KvpPluginMsgClient;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.MinecraftServer;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.network.packet.server.play.*;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class KloonPlayer extends Player implements ChestMenuPlayer {
    private static final Logger LOG = LoggerFactory.getLogger(KloonPlayer.class);
    private static final AtomicInteger COUNTER = new AtomicInteger();

    private final KvpPluginMsgClient proxyPluginMessageClient;

    private KloonAccount account;
    private KloonProxyInfo proxyInfo = KloonProxyInfo.DEFAULT;

    protected final KloonTablist tablist;
    protected final KloonSidebar sidebar;
    protected final KloonHeaderFooter headerFooter;
    protected final ActionBarQueue actionBar;

    private final int creationCount = COUNTER.incrementAndGet();
    private Team team;
    private boolean softDisconnect = false;

    private final VirtualTablist virtualTablist;
    private SignUX signUX;

    public KloonPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
        super(playerConnection, gameProfile);
        this.proxyPluginMessageClient = new KvpPluginMsgClient(this::sendPluginMessage);
        this.virtualTablist = new VirtualTablist(this);

        this.tablist = createTabList();
        this.sidebar = createSidebar();
        this.headerFooter = createHeaderFooter();
        this.actionBar = new ActionBarQueue(this);

        AnnotationEventsRegisterer events = new AnnotationEventsRegisterer(eventNode());
        events.register(new VirtualTablistListener(this, virtualTablist));
    }

    protected KloonSidebar createSidebar() {
        return new DefaultSidebar(this);
    }

    protected KloonHeaderFooter createHeaderFooter() {
        return new DefaultHeaderFooter(this);
    }

    protected KloonTablist createTabList() {
        return new DefaultTablist(this);
    }

    public KloonTablist getTabList() {
        return tablist;
    }

    public ActionBarQueue getActionBar() {
        return actionBar;
    }

    public void setAccount(KloonAccount account) {
        this.account = account;
    }

    public KloonAccount getAccount() {
        return account;
    }

    public KloonMoniker getMoniker() {
        return account.moniker();
    }

    public String getDisplayMM() {
        return account.getDisplayMM();
    }

    public String getColoredMM() {
        return account.getColoredMM();
    }

    public PlayerRankCache getRanks() {
        return account.ranks();
    }

    public ObjectId getAccountId() {
        return account.getId();
    }

    public void setProxyInfo(KloonProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
    }

    public KloonProxyInfo getProxyInfo() {
        return proxyInfo;
    }

    public KvpPluginMsgClient getProxyClient() {
        return proxyPluginMessageClient;
    }

    public VirtualTablist getVirtualTablist() {
        return virtualTablist;
    }

    public int getCreationCount() {
        return creationCount;
    }

    @Override
    public SignUX getSignUX() {
        return signUX;
    }

    @Override
    public void setSignUX(SignUX signUX) {
        this.signUX = signUX;
    }

    public PlayerSettingsStorage getSettingsStorage() {
        return new PlayerSettingsStorage(this);
    }

    public boolean isEnabled(SettingsToggle toggle) {
        try {
            return toggle.isEnabled().apply(getSettingsStorage());
        } catch (Throwable t) {
            LOG.error("Error getting setting", t);
            return toggle.defaultValue();
        }
    }

    public TransferPlayer toTransferPlayer() {
        return new TransferPlayer(getUuid(), getAccountId().toHexString(), account.getPreferredDatacenter().getDbKey());
    }

    public void allocateAndTransfer(Function<TransferPlayer, CompletableFuture<BackendAllocReply>> allocateFunc) {
        TransferPlayer transferPlayer = toTransferPlayer();
        CompletableFuture<BackendAllocReply> allocate = allocateFunc.apply(transferPlayer);
        allocate.whenCompleteAsync((reply, t) -> {
            if (t != null) {
                sendMessage(MM."<red>There was an error allocating a slot while moving you!");
                LOG.error(STR."Exception allocating slot for transfer" , t);
                return;
            }

            TransferSlot transferSlot = reply.getTransferSlot();
            if (reply.isError() || transferSlot == null) {
                BackendAllocReply.Status status = reply.getStatus();
                if (status == BackendAllocReply.Status.PLAYER_THROTTLED) {
                    sendMessage(MM."<red>Your ability to request instances is on cooldown (for a short while!)");
                } else if (status == BackendAllocReply.Status.NO_MATCH) {
                    sendMessage(MM."<red>Couldn't find an instance to move you to!");
                } else {
                    sendMessage(MM."<red>There was an error allocating a slot while moving you!");
                }
                LOG.error(STR."Error allocating slot for transfer: \{status}");
                return;
            }

            ApprovedTransfer transfer = new ApprovedTransfer(transferSlot);
            executeTransfer(transfer);
        }, scheduler());
    }

    public void executeTransfer(ApprovedTransfer transfer) {
        AllocationSlotsListener allocListener = Kgs.INSTANCE.getAllocationSlotsListener();
        if (allocListener.isLocalTransfer(transfer)) {
            sendMessage(MM."<gray>Transferring you to another instance on this server...");
            allocListener.execLocalTransfer(this, transfer);
        } else {
            TransferSlot transferSlot = transfer.getSlot();
            sendMessage(MM."<gray>Transferring you to \{transferSlot.serverDisplayName()}...");
            LOG.info(STR."Sent transfer for slot \{this} to proxy using \{transferSlot}");
            proxyPluginMessageClient.transfer(transferSlot.serverAllocation(), transferSlot.slotId());
        }
    }

    @Override
    public final void tick(long time) {
        super.tick(time);

        try {
            if (!isOnline() || instance == null || removed) {
                return;
            }

            actionBar.tick();

            customTick(time);
        } catch (Throwable t) {
            LOG.error("Error in custom player tick", t);
        }
    }

    @Override
    public void spawn() {
        super.spawn();
        try {
            Kgs.INSTANCE.getTablistSync().removeRemoteTeam(getUuid());
            RankLooks bestRank = account.moniker().ranks().getBestRankLooks();
            Component prefix = bestRank.icon() == null
                    ? Component.empty()
                    : MM."<\{bestRank.iconColorHex()}>\{bestRank.icon()} ";
            String teamName = TablistSync.generateTeamName(account.moniker(), false);
            this.team = MinecraftServer.getTeamManager().createBuilder(teamName)
                    .prefix(prefix)
                    .teamColor(NamedTextColor.WHITE)
                    .collisionRule(TeamsPacket.CollisionRule.NEVER)
                    .build();
            team.addMember(getUsername());
            Kgs.getCaches().playerBlocks().invalidate(getAccountId());
        } catch (Throwable t) {
            LOG.error("Error setting up player team " + getUsername() + " " + getUuid(), t);
        }
    }

    @Override
    protected void despawn() {
        super.despawn();
        if (team != null) {
            MinecraftServer.getTeamManager().deleteTeam(team);
            this.team = null;
        }
        Kgs.getCaches().playerBlocks().invalidate(getAccountId());
    }

    public void softDisconnect() {
        this.softDisconnect = true;
        scheduleTicks(() -> kick("Soft disconnected."), 12);
    }

    @Override
    public void interpretPacketQueue() {
        if (softDisconnect) {
            return;
        }
        super.interpretPacketQueue();
    }

    protected void customTick(long time) {
        sidebar.tick();
        headerFooter.tick();
    }

    public void sendWorldBorder(Point center, double diameter) {
        sendWorldBorder(new WorldBorder(diameter, center.blockX(), center.blockZ(), 0, 0));
    }

    public void sendWorldBorder(WorldBorder border) {
        sendPacket(new WorldBorderCenterPacket(border.centerX(), border.centerZ()));
        sendPacket(new WorldBorderSizePacket(border.diameter()));
        sendPacket(new WorldBorderWarningReachPacket(border.warningDistance()));
        sendPacket(new WorldBorderWarningDelayPacket(border.warningTime()));
    }

    public CoolSound playSound(SoundEvent soundEvent, double pitch) {
        return playSound(soundEvent, pitch, 1);
    }

    public CoolSound playSound(SoundEvent soundEvent, double pitch, double volume) {
        Sound sound = Sound.sound(soundEvent, Sound.Source.PLAYER, (float) volume, (float) pitch);
        playSound(sound);
        return new CoolSound(soundEvent, Pitch.base(pitch), volume);
    }

    public CoolSound playSound(SoundEvent soundEvent, Pitch pitch) {
        return playSound(soundEvent, pitch, 1.0);
    }

    public CoolSound playSound(SoundEvent soundEvent, Pitch pitch, double volume) {
        return playSound(soundEvent, pitch.compute(), volume);
    }

    public CoolSound playSound(CoolSound coolSound) {
        playSound(coolSound.soundEvent(), coolSound.pitch(), coolSound.volume());
        return coolSound;
    }

    public Component sendPit(NamedTextColor color, String subject, Component details) {
        Color convertedColor = new Color(color.red(), color.green(), color.blue());
        sendPit(convertedColor, subject, details);
        return details;
    }

    public Component sendPit(RGBLike color, String subject, Component details) {
        Color convertedColor = new Color(color.red(), color.green(), color.blue());
        sendPit(convertedColor, subject, details);
        return details;
    }

    public Component sendPitError(Component details) {
        return sendPit(NamedTextColor.DARK_RED, "ERROR!", details);
    }

    public void sendMessage(List<Component> components) {
        for (Component component : components) {
            sendMessage(component);
        }
    }

    public void scheduleTicks(Runnable runnable, int ticks) {
        TaskSchedule delay = ticks == 0 ? TaskSchedule.immediate() : TaskSchedule.tick(ticks);
        scheduler().scheduleTask(runnable, delay, TaskSchedule.stop());
    }

    public CompletableFuture<Boolean> hasBlocked(ObjectId targetAccountId) {
        return Kgs.getCaches().playerBlocks().hasBlocked(getAccountId(), targetAccountId);
    }

    public CompletableFuture<Boolean> isBlockedBy(ObjectId issuerAccountId) {
        return Kgs.getCaches().playerBlocks().hasBlocked(issuerAccountId, getAccountId());
    }

    public InventoryExtras getInventoryExtras() {
        return new InventoryExtras(this);
    }

    @Override
    public @UnknownNullability KloonInstance getInstance() {
        return (KloonInstance) super.getInstance();
    }

    public boolean isAuthorized(String resource) {
        if (getUsername().equals("Minikloon")) return true;
        return false;
    }

    // TODO: first arg should be TextColor
    public Component sendPit(Color color, String subject, Component details) {
        String hexColor = ColorFmt.toHex(color);

        Component msg = Component.empty();

        String subjectFmt = subject.toUpperCase();
        if (!subjectFmt.endsWith("!")) {
            subjectFmt = subjectFmt + "!";
        }

        msg = msg.append(MM."<b><\{hexColor}>\{subjectFmt}</\{hexColor}></b> ");
        msg = msg.append(details);

        sendMessage(msg);

        return details;
    }

    public Vec getLookVec() {
        return getPosition().direction();
    }

    public CardinalDirection getLookDir() {
        return CardinalDirection.closestDir(getLookVec());
    }

    public Pos getEyePosition() {
        return getPosition().add(0, getEyeHeight(), 0);
    }

    public Ray getEyeRay() {
        return Ray.fromPos(getEyePosition());
    }

    @Override
    public String toString() {
        return STR."[\{getUsername()} \{getUuid()}]";
    }
}
