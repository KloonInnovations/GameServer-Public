package io.kloon.gameserver.minestom.blocks.placement.generic;

import io.kloon.gameserver.minestom.blocks.properties.IntProp;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FourCounterPlacement extends BlockPlacementRule {
    private final IntProp counter;

    private boolean waterlogged;

    public FourCounterPlacement(Block block, IntProp counter) {
        super(block);
        this.counter = counter;
    }

    public FourCounterPlacement waterlogged() {
        this.waterlogged = true;
        return this;
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = state.block();
        if (waterlogged) {
            block = PlacementUtils.waterlogged(state);
        }

        Block existing = state.instance().getBlock(state.placePosition());
        int count = counter.getOrZero(existing) + 1;

        block = counter.get(count).on(block);

        return block;
    }

    @Override
    public boolean isSelfReplaceable(@NotNull BlockPlacementRule.Replacement replacement) {
        Block block = replacement.block();
        Block blockType = block.defaultState();
        if (blockType == this.block) {
            int flowers = counter.get(block);
            return flowers < 4;
        }
        return super.isSelfReplaceable(replacement);
    }
}
