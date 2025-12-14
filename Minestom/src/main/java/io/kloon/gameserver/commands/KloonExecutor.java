package io.kloon.gameserver.commands;

import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandExecutor;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public abstract class KloonExecutor implements CommandExecutor {
    @Override
    public final void apply(@NotNull CommandSender sender, @NotNull CommandContext context) {
        if (!(sender instanceof KloonPlayer player)) {
            sender.sendMessage(MM."<red>You need to be a player to use this command!");
            return;
        }

        apply(player, context);
    }

    public abstract void apply(@NotNull KloonPlayer player, @NotNull CommandContext context);
}
