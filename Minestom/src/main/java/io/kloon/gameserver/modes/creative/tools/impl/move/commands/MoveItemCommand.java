package io.kloon.gameserver.modes.creative.tools.impl.move.commands;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.prebuilt.ToolNumberExecutor;
import io.kloon.gameserver.modes.creative.tools.impl.move.MoveTool;
import io.kloon.gameserver.modes.creative.tools.impl.move.menu.MoveDistanceButton;
import io.kloon.gameserver.modes.creative.tools.impl.move.menu.MoveToolMenu;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;

public class MoveItemCommand extends ToolItemCommand<MoveTool> {
    public MoveItemCommand(MoveTool tool) {
        super(tool);

        ArgumentInteger distanceArg = ArgumentType.Integer("distance (blocks)");
        addSyntax(new ToolNumberExecutor<>(tool, MoveDistanceButton.DISTANCE_BLOCKS, distanceArg) {
            @Override
            public void modifyToolData(CreativePlayer player, MoveTool.Settings settings, MoveTool.Preferences pref, CommandContext context) {
                super.modifyToolData(player, settings, pref, context);
                settings.moveMultiples = false;
            }
        }, ArgumentType.Literal("blocks"), distanceArg);

        ArgumentInteger multiplesArg = ArgumentType.Integer("distance (multiples)");
        addSyntax(new ToolNumberExecutor<>(tool, MoveDistanceButton.DISTANCE_MULTIPLES, multiplesArg) {
            @Override
            public void modifyToolData(CreativePlayer player, MoveTool.Settings settings, MoveTool.Preferences pref, CommandContext context) {
                super.modifyToolData(player, settings, pref, context);
                settings.moveMultiples = true;
            }
        }, ArgumentType.Literal("multiples"), multiplesArg);

        addSyntaxToggleSetting("cut", MoveToolMenu.CUT);
        addSyntaxToggleSetting("ignore air", MoveToolMenu.IGNORE_AIR);
        addSyntaxToggleSetting("ignore masks", MoveToolMenu.IGNORE_MASKS);
    }
}
