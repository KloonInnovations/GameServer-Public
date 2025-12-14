package io.kloon.gameserver.creative.storage.owner.player;

import com.mongodb.client.model.Filters;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.creative.storage.owner.WorldOwner;
import io.kloon.gameserver.creative.storage.owner.WorldOwnerStorage;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.mongo.accounts.KloonAccount;
import io.kloon.infra.redis.RedisLock;
import io.kloon.infra.redis.RedisRateLimit;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import redis.clients.jedis.JedisPool;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class PlayerWorldOwner implements WorldOwner {
    private final ObjectId accountId;

    public PlayerWorldOwner(ObjectId accountId) {
        this.accountId = accountId;
    }

    public PlayerWorldOwner(KloonAccount account) {
        this(account.getId());
    }

    public PlayerWorldOwner(KloonPlayer player) {
        this(player.getAccount());
    }

    public ObjectId getAccountId() {
        return accountId;
    }

    @Override
    public Bson getQueryFilter() {
        return Filters.eq("ownership.playerId", accountId);
    }

    @Override
    public WorldOwnerStorage asOwnership() {
        return new WorldOwnerStorage(accountId);
    }

    @Override
    public RedisLock getEditLock(JedisPool redis) {
        return new RedisLock(redis, "worldlists:" + accountId.toHexString(), 30, TimeUnit.SECONDS);
    }

    @Override
    public RedisRateLimit getDeletionRateLimit(JedisPool redis) {
        return new RedisRateLimit(redis, "world_dels:" + accountId.toHexString(), 20, TimeUnit.DAYS);
    }

    @Override
    public CompletableFuture<WorldOwner.Loaded> loadAsyncStuff() {
        return Kgs.getCaches().monikers().getByAccountId(accountId)
                .thenApply(moniker -> new LoadedPlayerWorldOwner(this, moniker));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerWorldOwner that = (PlayerWorldOwner) o;
        return Objects.equals(accountId, that.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountId);
    }
}
