package io.kloon.gameserver.minestom.blocks.transforms.impl;

import io.kloon.gameserver.minestom.blocks.handlers.FacingXZBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.blocks.transforms.BlockTransformer;
import io.kloon.gameserver.minestom.blocks.transforms.FlipTransform;
import io.kloon.gameserver.minestom.blocks.transforms.RotationTransform;
import io.kloon.gameserver.util.coordinates.Axis;
import net.minestom.server.instance.block.Block;

public class FacingTransformer implements BlockTransformer {
    @Override
    public Block rotate(Block block, RotationTransform rotation) {
        FacingXZ facing = FacingXZBlock.FACING_XZ.get(block);
        facing = facing.rotate(rotation);
        return FacingXZBlock.FACING_XZ.get(facing).on(block);
    }

    @Override
    public Block flip(Block block, FlipTransform flip) {
        FacingXZ facing = FacingXZBlock.FACING_XZ.get(block);
        facing = facing.flip(flip);
        return FacingXZBlock.FACING_XZ.get(facing).on(block);
    }
}
