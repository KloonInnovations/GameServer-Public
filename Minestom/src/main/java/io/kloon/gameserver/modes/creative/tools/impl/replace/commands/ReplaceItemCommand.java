package io.kloon.gameserver.modes.creative.tools.impl.replace.commands;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.patterns.ArgumentPattern;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolEditFx;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemCommand;
import io.kloon.gameserver.modes.creative.commands.tools.api.give.ToolItemExecutor;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.tools.impl.replace.ReplaceTool;
import io.kloon.gameserver.modes.creative.tools.impl.replace.replacementconfig.ReplacementConfig;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentBlockState;
import net.minestom.server.instance.block.Block;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ReplaceItemCommand extends ToolItemCommand<ReplaceTool> {
    public ReplaceItemCommand(ReplaceTool tool) {
        super(tool);

        ArgumentBlockState fromArg = ArgumentType.BlockState("from");
        ArgumentPattern toArg = ArgumentPattern.create("to");
        addSyntax(new ToolItemExecutor<>(tool, ToolDataType.ITEM_BOUND) {
            @Override
            public void modifyToolData(CreativePlayer player, ReplaceTool.Settings settings, ReplaceTool.Preferences pref, CommandContext context) {
                Block from = context.get(fromArg);
                CreativePattern to = context.get(toArg);

                boolean exact = TinkeredBlock.is(from);

                ReplacementConfig replacementConfig = settings.getReplacementSafe();
                replacementConfig.put(from, to, exact);
                settings.setReplacement(replacementConfig);
            }

            @Override
            public ToolEditFx createEditFx(CreativePlayer player, ReplaceTool.Settings settings, ReplaceTool.Preferences pref, CommandContext context) {
                Block from = context.get(fromArg);
                CreativePattern to = context.get(toArg);
                return new ToolEditFx(
                        MM."<gray>Added replacing \{TinkeredBlock.getNameMM(from)} <gray>into \{to.labelMM()}<gray>!",
                        SoundEvent.BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE, Pitch.rng(1.2, 0.4)
                );
            }
        }, ArgumentType.Literal("add"), fromArg, toArg);
    }
}
