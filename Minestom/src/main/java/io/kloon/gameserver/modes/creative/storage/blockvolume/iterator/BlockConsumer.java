package io.kloon.gameserver.modes.creative.storage.blockvolume.iterator;

import net.minestom.server.instance.block.Block;

public interface BlockConsumer {
    // world coordinates
    void consume(int x, int y, int z, Block block);
}
