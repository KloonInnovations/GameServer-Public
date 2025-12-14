package io.kloon.gameserver.player.loading;

import com.spotify.futures.CompletableFutures;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.minestom.events.EventHandler;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.mongo.accounts.AccountsRepo;
import io.kloon.infra.mongo.accounts.KloonAccount;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent;
import net.minestom.server.network.player.PlayerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PlayerLoadListener {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerLoadListener.class);

    private final AccountsRepo accountsRepo;

    private final List<PlayerDataLoader> loaders = new ArrayList<>();

    public PlayerLoadListener(AccountsRepo accountsRepo) {
        this.accountsRepo = accountsRepo;
    }

    public <T> void addLoader(PlayerDataLoader loader) {
        loaders.add(loader);
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        PlayerConnection connection = event.getConnection();
        UUID playerUuid = event.getPlayerUuid();

        KloonAccount account;
        try {
            account = accountsRepo.get(playerUuid).get(600, TimeUnit.MILLISECONDS);
        } catch (Throwable t) {
            connection.kick(MM."<dark_red>Internal Error!");
            LOG.error(STR."Error getting player account for \{playerUuid}", t);
            return;
        }

        Kgs.INSTANCE.getFirstConfigProcessor().runAsyncAfterLogin(connection, "load account", (player, _) -> {
            player.setAccount(account);

            try {
                List<CompletableFuture<?>> futures = new ArrayList<>();
                for (PlayerDataLoader loader : loaders) {
                    CompletableFuture<?> future = loader.load(player, account);
                    futures.add(future);
                }
                CompletableFutures.allAsList(futures).get(600, TimeUnit.MILLISECONDS);
            } catch (Throwable t) {
                connection.kick(MM."<dark_red>Internal Error!");
                LOG.error(STR."Error getting extra player data for \{player}", t);
            }
        });
    }
}
