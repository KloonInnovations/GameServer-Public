package io.kloon.gameserver.modes.creative.tools.impl.copypaste.commands;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationUsage;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteTool;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CopyCommand extends ToolOperationCommand<CopyPasteTool> {
    public CopyCommand(CopyPasteTool tool) {
        super(tool, "copy");
        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                tool.copySelectionToClipboard(player);
            }
        });
    }

    @Override
    public List<ToolOperationUsage> getUsages() {
        return List.of(
                new ToolOperationUsage("", "Copies your <selection> <gray>into your clipboard.")
        );
    }
}
