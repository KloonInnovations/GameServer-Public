package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.BannerBlock;
import io.kloon.gameserver.minestom.blocks.handlers.FacingXZBlock;
import io.kloon.gameserver.minestom.blocks.properties.BannerRotProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BannerPlacement extends BlockPlacementRule {
    public BannerPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        BlockFace face = state.blockFace();
        if (face == BlockFace.BOTTOM) {
            return null;
        }

        float yaw = state.playerPosition().yaw();

        if (face == BlockFace.TOP) {
            int segment = BannerRotProp.convertToSegment(yaw + 180);
            return BannerBlock.ROTATION.get(segment).on(state);
        } else {
            FacingXZ facing = FacingXZ.fromBlockFace(state.blockFace());
            Block wallBlock = BannerBlock.BLOCK_TO_WALL.get(state.block().defaultState());
            return FacingXZBlock.FACING_XZ.get(facing).on(wallBlock);
        }
    }
}
