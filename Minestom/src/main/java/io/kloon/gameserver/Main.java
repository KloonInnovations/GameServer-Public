package io.kloon.gameserver;

import io.kloon.bigbackend.client.admin.GameServerSyncClient;
import io.kloon.bigbackend.gameservers.RemoveGameServerNotification;
import io.kloon.gameserver.backend.GameServerInfo;
import io.kloon.gameserver.modes.GameModeFactory;
import io.kloon.gameserver.modes.GameServerMode;
import io.kloon.gameserver.modes.ModeType;
import io.kloon.infra.KloonApp;
import io.kloon.infra.KloonEnv;
import io.kloon.infra.KloonNetworkInfra;
import io.prometheus.metrics.exporter.httpserver.HTTPServer;
import io.prometheus.metrics.instrumentation.jvm.JvmMetrics;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        JvmMetrics.builder().register();

        ModeType modeType = GsEnv.GAMEMODE.getEnum(ModeType.BY_MODEKEY, ModeType.DEV);

        KloonNetworkInfra infra;
        try {
            KloonApp app = new KloonApp("GameServer")
                    .withParam("gamemode", modeType.getDbKey());
            infra = KloonNetworkInfra.initializeSync(app);
        } catch (Throwable t) {
            LOG.error("Error setting up infra", t);
            Runtime.getRuntime().exit(1);
            return;
        }

        boolean proxied = GsEnv.USE_PROXY.getBoolean(false);
        Auth auth = proxied
                ? new Auth.Bungee()
                : new Auth.Online();
        MinecraftServer mcServer = MinecraftServer.init(auth);

        GameModeFactory gameModeFactory = new GameModeFactory();
        GameServerMode gameServerMode;
        try {
            gameServerMode = gameModeFactory.create(modeType, infra);
            gameServerMode.onMinestomInitialize();
        } catch (Throwable t) {
            LOG.error(STR."Error creating mode \{modeType}", t);
            Runtime.getRuntime().exit(1);
            return;
        }

        int minecraftPort = GsEnv.MINECRAFT_PORT.getInt(25565);

        int metricsPort = KloonEnv.METRICS_PORT.getInt(-1);
        if (metricsPort > 0) {
            try {
                LOG.info(STR."Starting metrics HTTP server on port \{metricsPort}");
                HTTPServer.builder().port(metricsPort).buildAndStart();
            } catch (Throwable t) {
                LOG.error("Error starting metrics HTTP server", t);
                System.exit(1);
                return;
            }
        }

        GameServerInfo gsInfo = new GameServerInfo(
                infra.serverName(),
                infra.allocationName(),
                minecraftPort,
                infra.datacenter(),
                modeType,
                System.currentTimeMillis(),
                proxied
        );

        KloonGameServer gameServer = new KloonGameServer(mcServer, infra, gsInfo, gameServerMode);
        Kgs.INSTANCE = gameServer;
        try {
            gameServer.start();
        } catch (Throwable t) {
            LOG.error("Error starting gameserver", t);
            Runtime.getRuntime().exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                LOG.info("Shutting down KloonGameServer...");
                GameServerSyncClient syncClient = new GameServerSyncClient(infra.nats());
                syncClient.notifyRemoval(new RemoveGameServerNotification(infra.allocationName()));
                infra.nats().drain(Duration.ofMillis(200)).get();
                infra.nats().close();
                LOG.info("We're done.");
            } catch (Throwable t) {
                LOG.error("Error during shutdown hook", t);
            }
        }));
    }
}
