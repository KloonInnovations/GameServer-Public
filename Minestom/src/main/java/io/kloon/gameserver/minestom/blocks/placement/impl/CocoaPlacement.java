package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.CocoaBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CocoaPlacement extends BlockPlacementRule {
    public CocoaPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        BlockFace clickedFace = state.blockFace();
        if (clickedFace == BlockFace.TOP || clickedFace == BlockFace.BOTTOM) {
            return null;
        }

        FacingXZ facing = FacingXZ.fromBlockFace(clickedFace.getOppositeFace());
        return CocoaBlock.FACING.get(facing).on(state.block());
    }
}
