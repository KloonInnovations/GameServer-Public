package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.SnowLayerBlock;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SnowLayerPlacement extends BlockPlacementRule {
    public SnowLayerPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block existing = state.instance().getBlock(state.placePosition());
        if (existing.id() == block.id()) {
            int layer = SnowLayerBlock.LAYERS.get(existing) + 1;
            return SnowLayerBlock.LAYERS.get(layer).on(existing);
        }

        return state.block();
    }

    @Override
    public boolean isSelfReplaceable(@NotNull BlockPlacementRule.Replacement replacement) {
        Block block = replacement.block();
        int layer = SnowLayerBlock.LAYERS.getOrZero(block);
        if (layer <= 1) {
            return true;
        }

        return replacement.blockFace() == BlockFace.TOP && layer < 8;
    }
}
