package io.kloon.gameserver.modes.creative.tools.impl.move.commands;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationUsage;
import io.kloon.gameserver.modes.creative.tools.impl.move.MoveTool;
import io.kloon.gameserver.util.coordinates.CardinalDirection;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MoveCommand extends ToolOperationCommand<MoveTool> {
    public MoveCommand(MoveTool tool) {
        super(tool);

        ArgumentInteger blocksArg = ArgumentType.Integer("blocks");
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                int blocks = context.get(blocksArg);
                CardinalDirection dir = player.getLookDir();
                tool.moveSelection(player, dir, blocks, true);
            }
        }, blocksArg);
    }

    @Override
    public List<ToolOperationUsage> getUsages() {
        return List.of(
                new ToolOperationUsage("<blocks>", "Moves your selection and its blocks in the direction you're facing")
        );
    }
}
