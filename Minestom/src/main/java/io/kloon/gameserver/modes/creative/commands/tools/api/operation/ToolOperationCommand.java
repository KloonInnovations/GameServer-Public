package io.kloon.gameserver.modes.creative.commands.tools.api.operation;

import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import net.minestom.server.command.builder.Command;

import java.util.List;

public abstract class ToolOperationCommand<T extends CreativeTool> extends Command implements CommandWithUsage {
    protected final T tool;

    public ToolOperationCommand(T tool) {
        super("/" + tool.getType().getCommandLabel());
        this.tool = tool;
    }

    public ToolOperationCommand(T tool, String name, String... aliases) {
        super("/" + name, aliases);
        this.tool = tool;
    }

    public ToolOperationCommand(T tool, String actualName, boolean what, String... aliases) {
        super(actualName, aliases);
        this.tool = tool;
    }

    public abstract List<ToolOperationUsage> getUsages();
}
