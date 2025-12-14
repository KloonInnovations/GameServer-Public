package io.kloon.gameserver.minestom.blocks.transforms;

import net.minestom.server.instance.block.Block;

public interface BlockTransformer {
    Block rotate(Block block, RotationTransform rotation);

    Block flip(Block block, FlipTransform flip);
}
