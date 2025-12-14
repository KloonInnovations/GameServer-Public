package io.kloon.gameserver;

import io.kloon.bigbackend.client.BigBackendClient;
import io.kloon.bigbackend.client.games.CreativeClient;
import io.kloon.discord.client.DiscordNatsClient;
import io.kloon.gameserver.backend.ChatSync;
import io.kloon.gameserver.backend.GameServerInfo;
import io.kloon.gameserver.backend.ServerSyncTask;
import io.kloon.gameserver.chestmenus.ChestMenuListeners;
import io.kloon.gameserver.creative.storage.CreativeWorldsRepo;
import io.kloon.gameserver.creative.storage.WorldListsCache;
import io.kloon.gameserver.minestom.GlobalMinestomTicker;
import io.kloon.gameserver.minestom.KloonInstance;
import io.kloon.gameserver.minestom.events.AnnotationEventsRegisterer;
import io.kloon.gameserver.minestom.events.EventHandler;
import io.kloon.gameserver.minestom.blocks.KloonPlacementRules;
import io.kloon.gameserver.minestom.net.KloonFirstConfigProcessor;
import io.kloon.gameserver.modes.GameServerMode;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.player.loading.PlayerLoadListener;
import io.kloon.gameserver.player.proxyinfo.ProxyInfoQueryListener;
import io.kloon.gameserver.service.GameServerService;
import io.kloon.gameserver.service.SpecificServerService;
import io.kloon.gameserver.service.TablistSync;
import io.kloon.gameserver.service.allocations.AllocationSlotsListener;
import io.kloon.gameserver.service.allocations.NoProxyLoginListener;
import io.kloon.gameserver.service.allocations.TransferSlotUsedEvent;
import io.kloon.infra.KloonNetworkInfra;
import io.kloon.infra.mongo.accounts.AccountsRepo;
import io.kloon.infra.mongo.blocks.BlockRepo;
import io.kloon.infra.serviceframework.ServiceBox;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class KloonGameServer {
    private static final Logger LOG = LoggerFactory.getLogger(KloonGameServer.class);

    private final MinecraftServer mcServer;
    private final KloonNetworkInfra infra;
    private final GameServerInfo serverInfo;
    private final GameServerMode mode;

    private final SchedulerManager scheduler;
    private final InstanceManager instanceManager;

    private final ServiceBox serviceBox;
    private final BigBackendClient backendClient;
    private final CreativeClient creativeClient;
    private final DiscordNatsClient discordClient;

    private final AccountsRepo accountsRepo;
    private final BlockRepo blockRepo;
    private final KloonFirstConfigProcessor firstConfigProcessor;
    private final PlayerLoadListener playerLoadListener;

    private final CreativeWorldsRepo creativeWorldsRepo;
    private final WorldListsCache worldListsCache;

    private final AllocationSlotsListener allocationSlotsListener;
    private final TablistSync tablistSync;

    public KloonGameServer(MinecraftServer mcServer, KloonNetworkInfra infra, GameServerInfo serverInfo, GameServerMode mode) {
        this.mcServer = mcServer;
        this.infra = infra;
        this.serverInfo = serverInfo;
        this.mode = mode;

        this.scheduler = MinecraftServer.getSchedulerManager();
        this.instanceManager = MinecraftServer.getInstanceManager();

        this.serviceBox = new ServiceBox(infra);
        this.backendClient = new BigBackendClient(infra.nats());
        this.creativeClient = new CreativeClient(infra.nats());
        this.discordClient = new DiscordNatsClient(infra.nats());

        this.accountsRepo = new AccountsRepo(infra.mongo());
        this.blockRepo = new BlockRepo(infra.mongo());
        this.firstConfigProcessor = new KloonFirstConfigProcessor();
        this.playerLoadListener = new PlayerLoadListener(accountsRepo);

        this.creativeWorldsRepo = new CreativeWorldsRepo(infra);
        this.worldListsCache = new WorldListsCache(creativeClient, creativeWorldsRepo);

        this.allocationSlotsListener = new AllocationSlotsListener(instanceManager, serverInfo);

        this.tablistSync = new TablistSync(infra.nats(), scheduler);
    }

    public void start() {
        MinecraftServer.getExceptionManager().setExceptionHandler(e -> LOG.error("Error in Minestom game server", e));
        MinecraftServer.getConnectionManager().setPlayerProvider(KloonPlayer::new);

        scheduler.submitTask(new ServerSyncTask(infra.nats(), serverInfo, instanceManager));

        serviceBox.register(new GameServerService(this));
        serviceBox.register(new SpecificServerService(this));
        serviceBox.register(tablistSync);

        KloonPlacementRules.register();

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();

        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, firstConfigProcessor::handleFirstConfig);
        scheduler.scheduleTask(firstConfigProcessor::tick, TaskSchedule.nextTick(), TaskSchedule.tick(10));

        AnnotationEventsRegisterer events = new AnnotationEventsRegisterer();
        events.register(GlobalMinestomTicker.getInstance());
        if (serverInfo.proxied()) {
            events.register(allocationSlotsListener);
        } else {
            events.register(new NoProxyLoginListener(this));
        }

        events.register(playerLoadListener);
        events.register(new ProxyInfoQueryListener());

        events.register(new ChatSync(infra, backendClient.getChat()));
        events.register(tablistSync);

        events.register(new Object() {
            @EventHandler
            public void onJoinAnotherPlayer(TransferSlotUsedEvent event) {
                KloonPlayer transferredPlayer = event.getPlayer();
                KloonInstance instance = transferredPlayer.getInstance();
                UUID joining = event.getSlot().getJoiningPlayerId();
                if (joining == null) {
                    return;
                }

                String instanceName = instance.getCuteName();

                Entity joinedEntity = instance.getEntityByUuid(joining);
                if (!(joinedEntity instanceof KloonPlayer joinedPlayer)) {
                    transferredPlayer.sendPit(NamedTextColor.YELLOW, "WHERE THEY AT?", MM."<gray>You joined a player on \{instanceName}, but they're not here anymore!");
                    return;
                }

                transferredPlayer.sendPit(NamedTextColor.GREEN, "JOINED!", MM."<gray>You <green>/join<gray>ed \{joinedPlayer.getDisplayMM()} <gray>on \{instanceName}!");
                joinedPlayer.sendPit(NamedTextColor.GREEN, "JOINED!", MM."\{transferredPlayer.getDisplayMM()} <green>/join<gray>ed you on this instance!");
            }
        });

        ChestMenuListeners.registerGlobal();

        mode.onStart(this);

        if (!serverInfo.proxied()) {
            LOG.info("Server is in local mode, meaning DON'T connect through a MC proxy.");
        }
        LOG.info(STR."Starting \"\{infra.serverName()}\" Minecraft server on port \{serverInfo.minecraftPort()} with mode \{serverInfo.gamemode()}...");

        mcServer.start("0.0.0.0", serverInfo.minecraftPort());
    }

    public KloonNetworkInfra getInfra() {
        return infra;
    }

    public String getAllocName() {
        return infra.allocationName();
    }

    public GameServerInfo getServerInfo() {
        return serverInfo;
    }

    public GameServerMode getMode() {
        return mode;
    }

    public SchedulerManager getScheduler() {
        return scheduler;
    }

    public InstanceManager getInstanceManager() {
        return instanceManager;
    }

    public ServiceBox getServiceBox() {
        return serviceBox;
    }

    public AccountsRepo getAccountsRepo() {
        return accountsRepo;
    }

    public TablistSync getTablistSync() {
        return tablistSync;
    }

    public BlockRepo getBlockRepo() {
        return blockRepo;
    }

    public KloonFirstConfigProcessor getFirstConfigProcessor() {
        return firstConfigProcessor;
    }

    public PlayerLoadListener getPlayerLoadListener() {
        return playerLoadListener;
    }

    public BigBackendClient getBackend() {
        return backendClient;
    }

    public CreativeClient getCreative() {
        return creativeClient;
    }

    public DiscordNatsClient getDiscord() {
        return discordClient;
    }

    public CreativeWorldsRepo getCreativeWorldsRepo() {
        return creativeWorldsRepo;
    }

    public WorldListsCache getWorldListsCache() {
        return worldListsCache;
    }

    public AllocationSlotsListener getAllocationSlotsListener() {
        return allocationSlotsListener;
    }
}
