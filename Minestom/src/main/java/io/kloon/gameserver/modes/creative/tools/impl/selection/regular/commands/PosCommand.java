package io.kloon.gameserver.modes.creative.tools.impl.selection.regular.commands;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationUsage;
import io.kloon.gameserver.modes.creative.tools.impl.selection.regular.SelectionTool;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class PosCommand extends ToolOperationCommand<SelectionTool> {
    private final boolean first;

    public PosCommand(SelectionTool tool, String name, boolean first) {
        super(tool, name);
        this.first = first;

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                if (first) {
                    tool.setPos1(player);
                } else {
                    tool.setPos2(player);
                }
            }
        });
    }

    @Override
    public List<ToolOperationUsage> getUsages() {
        return Arrays.asList(
                new ToolOperationUsage("",
                        STR."Sets point #\{first ? "1" : "2"} of your <selection> <gray>to the block you're standing in.")
        );
    }
}
