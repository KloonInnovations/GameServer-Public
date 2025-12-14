package io.kloon.gameserver.modes.creative.tools.impl.selection.regular.commands;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationUsage;
import io.kloon.gameserver.modes.creative.tools.impl.selection.regular.SelectionTool;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DeselectCommand extends ToolOperationCommand<SelectionTool> {
    public DeselectCommand(SelectionTool tool) {
        super(tool, "deselect");
        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                tool.deselect(player);
            }
        });
    }

    @Override
    public List<ToolOperationUsage> getUsages() {
        return List.of(
                new ToolOperationUsage("", "<gray>If you have a <selection><gray>, you won't after this.")
        );
    }
}
