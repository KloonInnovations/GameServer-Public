package io.kloon.gameserver.modes.creative.tools.impl.stack.commands;

import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.prebuilt.ToolNumberExecutor;
import io.kloon.gameserver.modes.creative.tools.impl.stack.StackTool;
import io.kloon.gameserver.modes.creative.tools.impl.stack.menu.StackToolMenu;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;

public class StackItemCommand extends ToolItemCommand<StackTool> {
    public StackItemCommand(StackTool tool) {
        super(tool);

        ArgumentInteger stacksArg = ArgumentType.Integer("stacks (multiples)");
        addSyntax(new ToolNumberExecutor<>(tool, StackToolMenu.STACKS, stacksArg),
                ArgumentType.Literal("stacks"), stacksArg);

        ArgumentInteger offsetArg = ArgumentType.Integer("offset (blocks)");
        addSyntax(new ToolNumberExecutor<>(tool, StackToolMenu.OFFSET, offsetArg),
                ArgumentType.Literal("offset"), offsetArg);

        addSyntaxTogglePreference("autoselect", StackToolMenu.AUTO_SELECT);
    }
}
