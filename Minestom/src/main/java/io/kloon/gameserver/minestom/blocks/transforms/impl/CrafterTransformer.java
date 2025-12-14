package io.kloon.gameserver.minestom.blocks.transforms.impl;

import io.kloon.gameserver.minestom.blocks.handlers.CrafterBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FrontAndTop;
import io.kloon.gameserver.minestom.blocks.transforms.BlockTransformer;
import io.kloon.gameserver.minestom.blocks.transforms.FlipTransform;
import io.kloon.gameserver.minestom.blocks.transforms.RotationTransform;
import net.minestom.server.instance.block.Block;

public class CrafterTransformer implements BlockTransformer {
    @Override
    public Block rotate(Block block, RotationTransform rotation) {
        FrontAndTop orientation = CrafterBlock.ORIENTATION.get(block);

        FrontAndTop rotated = FrontAndTop.from(
                rotation.rotate(orientation.front()),
                rotation.rotate(orientation.top())
        );

        return CrafterBlock.ORIENTATION.get(rotated).on(block);
    }

    @Override
    public Block flip(Block block, FlipTransform flip) {
        return block; // TODO
    }
}
