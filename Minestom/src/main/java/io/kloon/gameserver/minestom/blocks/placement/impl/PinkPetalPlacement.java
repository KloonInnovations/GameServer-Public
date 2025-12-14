package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.PinkPetalBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PinkPetalPlacement extends BlockPlacementRule {
    public PinkPetalPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = state.block();

        Block existing = state.instance().getBlock(state.placePosition());
        int flowers = PinkPetalBlock.FLOWER_AMOUNT.getOrZero(existing) + 1;

        FacingXZ facing = FacingXZ.fromLook(state.playerPosition()).opposite();

        block = PinkPetalBlock.FLOWER_AMOUNT.get(flowers).on(block);
        block = PinkPetalBlock.FACING.get(facing).on(block);

        return block;
    }

    @Override
    public boolean isSelfReplaceable(@NotNull BlockPlacementRule.Replacement replacement) {
        Block block = replacement.block();
        Block blockType = block.defaultState();
        if (blockType == this.block) {
            int flowers = PinkPetalBlock.FLOWER_AMOUNT.get(block);
            return flowers < 4;
        }
        return super.isSelfReplaceable(replacement);
    }
}
