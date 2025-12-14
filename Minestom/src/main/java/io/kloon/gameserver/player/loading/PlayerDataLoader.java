package io.kloon.gameserver.player.loading;

import io.kloon.infra.mongo.accounts.KloonAccount;
import net.minestom.server.entity.Player;

import java.util.concurrent.CompletableFuture;

public interface PlayerDataLoader {
    CompletableFuture<?> load(Player player, KloonAccount account);
}
