package io.kloon.gameserver.modes.creative.masks.menu.editmask.block;

import net.minestom.server.instance.block.Block;

public interface MaskWithBlock {
    Block getBlock();

    void setBlock(Block block);
}
