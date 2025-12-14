package io.kloon.gameserver.modes.creative.tools.impl.fill.commands;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.commands.patterns.ArgumentPattern;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationUsage;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.tools.impl.fill.FillTool;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class FillCommand extends ToolOperationCommand<FillTool> {
    public static final String LABEL = "fill";

    public FillCommand(FillTool tool) {
        super(tool);

        ArgumentPattern patternArg = ArgumentPattern.create("block");
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                CreativePattern pattern = context.get(patternArg);
                tool.fillSelection(player, pattern);
            }
        }, patternArg);
    }

    @Override
    public List<ToolOperationUsage> getUsages() {
        return Arrays.asList(
                new ToolOperationUsage(
                        "<block>",
                        "Fills <selection> <gray>with block."
                )
        );
    }
}
