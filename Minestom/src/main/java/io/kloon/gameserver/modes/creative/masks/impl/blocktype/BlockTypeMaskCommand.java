package io.kloon.gameserver.modes.creative.masks.impl.blocktype;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.masks.api.MaskItemCommand;
import io.kloon.gameserver.modes.creative.commands.masks.api.MaskItemExecutor;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentBlockState;
import net.minestom.server.instance.block.Block;

public class BlockTypeMaskCommand extends MaskItemCommand<BlockTypeMask> {
    public BlockTypeMaskCommand(BlockTypeMask mask) {
        super(mask);

        ArgumentBlockState blockArg = ArgumentType.BlockState("block to match");
        addSyntax(new MaskItemExecutor<>(mask) {
            @Override
            public void modifyData(CreativePlayer player, BlockTypeMask.Data data, CommandContext context) {
                Block block = context.get(blockArg);
                data.setBlock(block);
            }
        }, blockArg);
    }
}
