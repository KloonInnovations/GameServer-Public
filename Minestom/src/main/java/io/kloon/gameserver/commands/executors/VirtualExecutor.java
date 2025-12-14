package io.kloon.gameserver.commands.executors;

import io.kloon.gameserver.minestom.scheduler.Sync;
import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandExecutor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public abstract class VirtualExecutor implements CommandExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(VirtualExecutor.class);
    private static final Executor EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public final void apply(@NotNull CommandSender sender, @NotNull CommandContext context) {
        if (!(sender instanceof KloonPlayer player)) {
            sender.sendMessage(MM."<red>You need to be a player to use this command!");
            return;
        }

        EXECUTOR.execute(() -> {
            try {
                apply(player, context);
            } catch (Throwable t) {
                player.sendMessage(MM."<red>There was an error executing that command!");
                LOG.error("Error executing command", t);
            }
        });
    }

    public abstract void apply(@NotNull KloonPlayer player, @NotNull CommandContext context);

    public CompletableFuture<Void> sync(Runnable runnable) {
        return Sync.run(runnable);
    }

    public <T> CompletableFuture<T> sync(Supplier<T> supplier) {
        return Sync.supply(supplier);
    }
}
