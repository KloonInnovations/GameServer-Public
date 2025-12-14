package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.FenceBlock;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FencePlacement extends BlockPlacementRule {
    public FencePlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @NotNull Block blockUpdate(@NotNull BlockPlacementRule.UpdateState state) {
        return getConnectionsState(state.instance(), state.currentBlock(), state.blockPosition());
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = PlacementUtils.waterlogged(state);
        Point blockPos = state.placePosition();
        return getConnectionsState(state.instance(), block, blockPos);
    }

    private Block getConnectionsState(Block.Getter instance, Block block, Point blockPos) {
        for (Direction direction : Direction.HORIZONTAL) {
            Point relPos = blockPos.add(direction.vec());
            Block relBlock = instance.getBlock(relPos);
            boolean canConnect = FenceBlock.BLOCKS.contains(relBlock.defaultState()) || FenceBlock.canAttach(relBlock);
            block = FenceBlock.fromDirection(direction).get(canConnect).on(block);
        }
        return block;
    }
}
