package io.kloon.gameserver.minestom.blocks.transforms.impl;

import io.kloon.gameserver.minestom.blocks.handlers.FacingXZBlock;
import io.kloon.gameserver.minestom.blocks.handlers.HopperBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingHopper;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.blocks.transforms.BlockTransformer;
import io.kloon.gameserver.minestom.blocks.transforms.FlipTransform;
import io.kloon.gameserver.minestom.blocks.transforms.RotationTransform;
import net.minestom.server.instance.block.Block;

public class HopperTransformer implements BlockTransformer {
    @Override
    public Block rotate(Block block, RotationTransform rotation) {
        FacingHopper facing = HopperBlock.FACING.get(block);
        facing = facing.rotate(rotation);
        return HopperBlock.FACING.get(facing).on(block);
    }

    @Override
    public Block flip(Block block, FlipTransform flip) {
        FacingHopper facing = HopperBlock.FACING.get(block);
        if (facing == FacingHopper.DOWN) {
            return block;
        }

        FacingXZ ehh = facing.toFacing();
        ehh = ehh.flip(flip);

        return FacingXZBlock.FACING_XZ.get(ehh).on(block);
    }
}
