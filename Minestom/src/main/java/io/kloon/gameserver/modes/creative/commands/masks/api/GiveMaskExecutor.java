package io.kloon.gameserver.modes.creative.commands.masks.api;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.masks.MaskType;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class GiveMaskExecutor extends CreativeExecutor {
    private final MaskType mask;
    private final boolean negated;

    public GiveMaskExecutor(MaskType<?> mask, boolean negated) {
        this.mask = mask;
        this.negated = negated;
    }

    @Override
    public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
        if (!player.canEditWorld()) {
            player.sendPit(NamedTextColor.RED, "CAN'T!", MM."<gray>You may not use this command without edit permissions!");
            return;
        }

        MaskWithData<?> data = mask.createDefault()
                .withNegated(negated);

        mask.giveToPlayer(player, data);
    }
}
