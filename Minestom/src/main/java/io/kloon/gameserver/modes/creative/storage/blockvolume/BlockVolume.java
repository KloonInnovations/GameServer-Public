package io.kloon.gameserver.modes.creative.storage.blockvolume;

import io.kloon.gameserver.modes.creative.storage.blockvolume.iterator.VolumeIterator;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

public interface BlockVolume {
    // world coordinates, null if outside bounds
    @Nullable
    Block getBlock(int x, int y, int z);

    VolumeIterator iterator();

    // number of blocks
    int count();

    BoundingBox toCuboid();
}
