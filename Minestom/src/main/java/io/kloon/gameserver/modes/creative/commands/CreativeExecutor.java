package io.kloon.gameserver.modes.creative.commands;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public abstract class CreativeExecutor implements CommandExecutor {
    @Override
    public final void apply(@NotNull CommandSender sender, @NotNull CommandContext context) {
        if (!(sender instanceof CreativePlayer player)) {
            sender.sendMessage(MM."<red>You need to be a player to use this command!");
            return;
        }

        boolean hasEditPerm = player.canEditWorld();
        if (!hasEditPerm && !canRunWithoutBuildPermissions(player)) {
            player.playSound(SoundEvent.BLOCK_NOTE_BLOCK_BIT, Pitch.base(0.5).addRand(0.45));
            player.sendPit(NamedTextColor.RED, "OOPS!", MM."<gray>Can't run that without permission to edit the world!");
            return;
        }

        apply(player, context);
    }

    public abstract void apply(@NotNull CreativePlayer player, @NotNull CommandContext context);

    public boolean canRunWithoutBuildPermissions(CreativePlayer player) {
        return false;
    }
}
