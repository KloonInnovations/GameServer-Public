package io.kloon.gameserver.modes.creative.masks.impl.exactblock;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.masks.api.MaskItemCommand;
import io.kloon.gameserver.modes.creative.commands.masks.api.MaskItemExecutor;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentBlockState;
import net.minestom.server.instance.block.Block;

public class ExactBlockMaskCommand extends MaskItemCommand<ExactBlockMask> {
    public ExactBlockMaskCommand(ExactBlockMask mask) {
        super(mask);

        ArgumentBlockState blockArg = ArgumentType.BlockState("block to match");
        addSyntax(new MaskItemExecutor<>(mask) {
            @Override
            public void modifyData(CreativePlayer player, ExactBlockMask.Data data, CommandContext context) {
                Block block = context.get(blockArg);
                data.setBlock(block);
            }
        }, blockArg);
    }
}
