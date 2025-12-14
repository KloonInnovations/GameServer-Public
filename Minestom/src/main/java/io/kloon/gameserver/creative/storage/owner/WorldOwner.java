package io.kloon.gameserver.creative.storage.owner;

import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.KloonNetworkInfra;
import io.kloon.infra.redis.RedisLock;
import io.kloon.infra.redis.RedisRateLimit;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

// needs to implement equals/hashcode
public interface WorldOwner {
    Logger LOG = LoggerFactory.getLogger(WorldOwner.class);

    Bson getQueryFilter();

    WorldOwnerStorage asOwnership();

    RedisLock getEditLock(JedisPool redis);

    RedisRateLimit getDeletionRateLimit(JedisPool redis);

    CompletableFuture<WorldOwner.Loaded> loadAsyncStuff();

    interface Loaded {
        String getPlayerListLabelMM();
    }

    default void runWithLock(KloonNetworkInfra infra, KloonPlayer player, Supplier<CompletableFuture<?>> futureSupplier) {
        RedisLock editLock = getEditLock(infra.redis());
        editLock.lock().exceptionally(t -> {
            LOG.error(STR."Error acquiring worlds list \{getQueryFilter()}", t);
            return false;
        }).thenComposeAsync(locked -> {
            if (locked) {
                player.sendPit(NamedTextColor.RED, "OOPS!", MM."<gray>The worlds list is busy being edited!");
                return CompletableFuture.completedFuture(null);
            }

            try {
                return futureSupplier.get();
            } catch (Throwable t) {
                throw new RuntimeException("Error creating future", t);
            }
        }, player.scheduler()).whenCompleteAsync((_, t) -> {
            if (t != null) {
                LOG.error("Error with future within lock", t);
            }
            editLock.unlock().exceptionally(t2 -> {
                LOG.error(STR."Error unlocking lock \{editLock.getKey()}", t2);
                return null;
            });
        });
    }
}
