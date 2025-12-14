package io.kloon.gameserver;

import io.kloon.bigbackend.client.BigBackendClient;
import io.kloon.bigbackend.client.games.CreativeClient;
import io.kloon.gameserver.creative.storage.CreativeWorldsRepo;
import io.kloon.gameserver.modes.ModeType;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.KloonNetworkInfra;
import io.kloon.infra.cache.KloonCaches;
import io.kloon.infra.mongo.accounts.AccountsRepo;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class Kgs {
    private static final Logger LOG = LoggerFactory.getLogger(Kgs.class);

    public static KloonGameServer INSTANCE = null;

    private Kgs() {}

    public static BigBackendClient getBackend() {
        return INSTANCE.getBackend();
    }

    public static CreativeClient getCreative() {
        return INSTANCE.getCreative();
    }

    public static KloonNetworkInfra getInfra() {
        return INSTANCE.getInfra();
    }

    public static CreativeWorldsRepo getCreativeRepos() {
        return INSTANCE.getCreativeWorldsRepo();
    }

    public static KloonCaches getCaches() {
        return getInfra().caches();
    }

    public static AccountsRepo getAccountsRepo() {
        return INSTANCE.getAccountsRepo();
    }

    public static ModeType getModeType() {
        return INSTANCE.getMode().getType();
    }

    public static Stream<KloonPlayer> streamPlayers() {
        ConnectionManager connMan = MinecraftServer.getConnectionManager();
        Stream.Builder<KloonPlayer> stream = Stream.builder();
        for (Player player : connMan.getOnlinePlayers()) {
            if (player instanceof KloonPlayer kp && kp.getInstance() != null) {
                stream.add(kp);
            }
        }
        return stream.build();
    }

    public static void broadcastMessage(String message) {
        LOG.info(STR."Broadcast: \{message}");
        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(player -> {
            player.sendMessage(message);
        });
    }
}
