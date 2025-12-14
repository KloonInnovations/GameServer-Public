package io.kloon.gameserver.modes.creative.tools.impl.stack.commands;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationUsage;
import io.kloon.gameserver.modes.creative.tools.impl.stack.StackTool;
import io.kloon.gameserver.util.coordinates.CardinalDirection;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StackCommand extends ToolOperationCommand<StackTool> {
    public StackCommand(StackTool tool) {
        super(tool);

        ArgumentInteger stacksArg = ArgumentType.Integer("stack");
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                int stacks = context.get(stacksArg);
                CardinalDirection dir = player.getLookDir();
                tool.stackSelection(player, dir.reverse(), true, stacks, 0);
            }
        }, stacksArg);

        ArgumentInteger offsetArg = ArgumentType.Integer("offset");
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                int stacks = context.get(stacksArg);
                int offset = context.get(offsetArg);
                CardinalDirection dir = player.getLookDir();
                tool.stackSelection(player, dir.reverse(), true, stacks, offset);
            }
        }, stacksArg, offsetArg);
    }

    @Override
    public List<ToolOperationUsage> getUsages() {
        return List.of(
                new ToolOperationUsage("<stacks>", "Copies your selection X times in your aimed direction")
        );
    }
}
