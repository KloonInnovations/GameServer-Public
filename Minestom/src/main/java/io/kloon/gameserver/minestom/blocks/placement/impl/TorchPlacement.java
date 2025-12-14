package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.TorchBlock;
import io.kloon.gameserver.minestom.blocks.properties.BlockProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TorchPlacement extends BlockPlacementRule {
    public TorchPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        BlockFace face = state.blockFace();
        if (face == BlockFace.BOTTOM) {
            return null;
        }
        if (face == BlockFace.TOP) {
            return block;
        }

        FacingXZ facing = FacingXZ.fromBlockFace(face);
        Block wallTorch = TorchBlock.TORCH_TO_WALL.get(block.defaultState());

        return BlockProp.FACING_XZ.get(facing).on(wallTorch);
    }
}
