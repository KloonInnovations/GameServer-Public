package io.kloon.gameserver.minestom.blocks.transforms.impl;

import io.kloon.gameserver.minestom.blocks.handlers.PillarBlock;
import io.kloon.gameserver.minestom.blocks.transforms.BlockTransformer;
import io.kloon.gameserver.minestom.blocks.transforms.FlipTransform;
import io.kloon.gameserver.minestom.blocks.transforms.RotationTransform;
import io.kloon.gameserver.util.coordinates.Axis;
import net.minestom.server.instance.block.Block;

public class AxisTransformer implements BlockTransformer {
    @Override
    public Block rotate(Block block, RotationTransform rotation) {
        Axis axis = PillarBlock.AXIS.get(block);
        Axis rotated = rotateAxis(axis, rotation);
        return PillarBlock.AXIS.get(rotated).on(block);
    }

    private Axis rotateAxis(Axis axis, RotationTransform rotation) {
        return switch (rotation) {
            case NONE, CLOCKWISE_180 -> axis;
            case CLOCKWISE_90, CLOCKWISE_270 -> {
                if (axis == Axis.X) {
                    yield Axis.Z;
                }
                if (axis == Axis.Z) {
                    yield Axis.X;
                }
                yield axis;
            }
        };
    }

    @Override
    public Block flip(Block block, FlipTransform flip) {
        return block;
    }
}
