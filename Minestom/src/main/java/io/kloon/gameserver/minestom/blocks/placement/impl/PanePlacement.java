package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.FenceBlock;
import io.kloon.gameserver.minestom.blocks.handlers.PaneBlock;
import io.kloon.gameserver.minestom.blocks.handlers.WallBlock;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PanePlacement extends BlockPlacementRule {
    public PanePlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @NotNull Block blockUpdate(@NotNull BlockPlacementRule.UpdateState state) {
        return getDirectionalState(state.instance(), state.currentBlock(), state.blockPosition());
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = PlacementUtils.waterlogged(state);
        return getDirectionalState(state.instance(), block, state.placePosition());
    }

    public Block getDirectionalState(Block.Getter instance, Block block, Point blockPos) {
        for (Direction direction : Direction.HORIZONTAL) {
            Block relBlock = instance.getBlock(blockPos.add(direction.vec())).defaultState();
            boolean canConnect = PaneBlock.BLOCKS.contains(relBlock)
                                 || WallBlock.BLOCKS.contains(relBlock)
                                 || FenceBlock.canAttach(relBlock);
            block = PaneBlock.fromDirection(direction).get(canConnect).on(block);
        }
        return block;
    }
}
