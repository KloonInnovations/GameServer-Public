package io.kloon.gameserver.modes.creative.commands.test;

import io.kloon.gameserver.commands.AdminCommand;
import io.kloon.gameserver.commands.KloonExecutor;
import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;

public class DimensionInfoCommand extends AdminCommand {
    public DimensionInfoCommand() {
        super("dimensioninfo");
        setDefaultExecutor(new KloonExecutor() {
            @Override
            public void apply(@NotNull KloonPlayer player, @NotNull CommandContext context) {
                DimensionType dimension = player.getInstance().getCachedDimensionType();
                player.sendMessage(STR."Min Y = \{dimension.minY()}");
                player.sendMessage(STR."Max Y = \{dimension.maxY()}");
                player.sendMessage(STR."Height = \{dimension.height()}");
            }
        });
    }
}
