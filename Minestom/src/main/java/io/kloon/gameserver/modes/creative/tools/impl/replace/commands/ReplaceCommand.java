package io.kloon.gameserver.modes.creative.tools.impl.replace.commands;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.commands.patterns.ArgumentPattern;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.operation.ToolOperationUsage;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.tools.impl.replace.ReplaceTool;
import io.kloon.gameserver.modes.creative.tools.impl.replace.replacementconfig.ReplacementConfig;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentBlockState;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplaceCommand extends ToolOperationCommand<ReplaceTool> {
    public ReplaceCommand(ReplaceTool tool) {
        super(tool);

        ArgumentBlockState fromArg = ArgumentType.BlockState("from");
        ArgumentPattern toArg = ArgumentPattern.create("to");
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                Block from = context.get(fromArg);
                CreativePattern to = context.get(toArg);
                ReplacementConfig replacementConfig = commandToReplacementConfig(from, to);
                tool.replaceSelection(player, replacementConfig);
            }
        }, fromArg, toArg);
    }

    private ReplacementConfig commandToReplacementConfig(Block from, CreativePattern to) {
        Map<Block, CreativePattern> exact = new HashMap<>();
        Map<Block, CreativePattern> any = new HashMap<>();

        if (TinkeredBlock.is(from)) {
            exact.put(from, to);
        } else {
            any.put(from, to);
        }

        return new ReplacementConfig(exact, any);
    }

    @Override
    public List<ToolOperationUsage> getUsages() {
        return List.of(
                new ToolOperationUsage("<from> <to>", "Replaces blocks in your <selection<gray>.")
        );
    }
}
