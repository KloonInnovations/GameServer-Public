package io.kloon.gameserver.minestom.scheduler;

import net.minestom.server.MinecraftServer;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class Sync {
    public static CompletableFuture<Void> run(Runnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        MinecraftServer.getSchedulerManager().scheduleEndOfTick(() -> {
            try {
                runnable.run();
                future.complete(null);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    public static <T> CompletableFuture<T> supply(Supplier<T> supplier) {
        CompletableFuture<T> future = new CompletableFuture<>();
        MinecraftServer.getSchedulerManager().scheduleEndOfTick(() -> {
            try {
                T value = supplier.get();
                future.complete(value);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }
}
