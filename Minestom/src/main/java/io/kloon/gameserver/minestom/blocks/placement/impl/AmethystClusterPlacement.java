package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import io.kloon.gameserver.minestom.blocks.properties.BlockProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXYZ;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AmethystClusterPlacement extends BlockPlacementRule {
    public AmethystClusterPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = PlacementUtils.waterlogged(state);
        FacingXYZ facing = FacingXYZ.fromBlockFace(state.blockFace());
        block = BlockProp.FACING_XYZ.get(facing).on(block);
        return block;
    }
}
