package io.kloon.gameserver.chestmenus.autoupdate;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.IsMenuOpen;
import io.kloon.infra.serviceframework.subscriptions.PredicatedSub;
import io.kloon.infra.serviceframework.subscriptions.PredicatedSubscriber;
import io.kloon.infra.util.cooldown.impl.TimeCooldown;
import net.minestom.server.entity.Player;

import java.time.temporal.TemporalUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class NatsSubCache<T> implements Supplier<T> {
    private final PredicatedSubscriber<T> subscriber;
    private final Supplier<CompletionStage<T>> fetcher;

    private TimeCooldown expiry = new TimeCooldown(5, TimeUnit.MINUTES);
    private TimeCooldown retry = new TimeCooldown(5, TimeUnit.SECONDS);

    private CompletableFuture<T> fetching;
    private T cache;

    public NatsSubCache(PredicatedSubscriber<T> subscriber, Supplier<CompletionStage<T>> fetcher) {
        this.subscriber = subscriber;
        this.fetcher = fetcher;
    }

    public NatsSubCache<T> expireAfter(long time, TemporalUnit unit) {
        this.expiry = new TimeCooldown(time, unit);
        return this;
    }

    public NatsSubCache<T> retryCooldown(long time, TemporalUnit unit) {
        this.retry = new TimeCooldown(time, unit);
        return this;
    }

    public PredicatedSub<T> sub(Player player, ChestMenu menu) {
        PredicatedSub<T> sub = new PredicatedSub<>(new IsMenuOpen(player, menu), this::setCache);
        subscriber.add(sub);
        return sub;
    }

    private void setCache(T obj) {
        this.cache = obj;
        this.expiry.cooldown();
    }

    public CompletableFuture<T> fetchAndGet() {
        if (!expiry.isOnCooldown() || cache == null) {
            this.fetching = fetch();
        }

        if (fetching.isCancelled() || fetching.isCompletedExceptionally()) {
            if (retry.cooldownIfPossible()) {
                this.fetching = fetch();
            }
        }

        return cache == null
                ? fetching
                : CompletableFuture.completedFuture(cache);
    }

    private CompletableFuture<T> fetch() {
        return fetcher.get().toCompletableFuture().thenApply(obj -> {
            setCache(obj);
            return obj;
        });
    }

    @Override
    public T get() {
        return cache;
    }
}
