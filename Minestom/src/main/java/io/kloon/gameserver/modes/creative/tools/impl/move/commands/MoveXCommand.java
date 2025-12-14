package io.kloon.gameserver.modes.creative.tools.impl.move.commands;

import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationUsage;
import io.kloon.gameserver.modes.creative.selection.TwoCuboidSelection;
import io.kloon.gameserver.modes.creative.tools.generics.ToolMessages;
import io.kloon.gameserver.modes.creative.tools.impl.move.MoveTool;
import io.kloon.gameserver.util.coordinates.Axis;
import io.kloon.gameserver.util.coordinates.CardinalDirection;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MoveXCommand extends ToolOperationCommand<MoveTool> {
    public MoveXCommand(MoveTool tool) {
        super(tool, "movex");

        ArgumentInteger multiplesArg = ArgumentType.Integer("multiples");
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                if (!(player.getSelection() instanceof TwoCuboidSelection selection)) {
                    ToolMessages.sendRequireSelection(player);
                    return;
                }

                int multiples = context.get(multiplesArg);

                CardinalDirection dir = player.getLookDir();
                Axis axis = dir.axis();

                BoundingBox selectionCuboid = selection.getCuboid();
                double size = BoundingBoxUtils.dimension(selectionCuboid, axis);
                int distance = (int) (multiples * size);

                tool.moveSelection(player, dir, distance, true);
            }
        }, multiplesArg);
    }

    @Override
    public List<ToolOperationUsage> getUsages() {
        return List.of(
                new ToolOperationUsage("<multiples>", "Moves your selection and its blocks in your facing direction")
        );
    }
}
