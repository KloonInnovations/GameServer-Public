package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.HugeMushroomBlock;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HugeMushroomPlacement extends BlockPlacementRule {
    public HugeMushroomPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @NotNull Block blockUpdate(@NotNull BlockPlacementRule.UpdateState state) {
        return getState(state.instance(), state.currentBlock(), state.blockPosition());
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        return getState(state.instance(), state.block(), state.placePosition());
    }

    private Block getState(Block.Getter instance, Block block, Point blockPos) {
        for (Direction direction : Direction.values()) {
            Block relBlock = instance.getBlock(blockPos.add(direction.vec()));
            if (block.id() == relBlock.id()) {
                block = HugeMushroomBlock.getFromDirection(direction).get(false).on(block);
            }
        }

        return block;
    }
}
