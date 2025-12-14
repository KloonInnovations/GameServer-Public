package io.kloon.gameserver.modes.creative.storage.blockvolume.impl;

import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolume;
import io.kloon.gameserver.modes.creative.storage.blockvolume.iterator.BlockConsumer;
import io.kloon.gameserver.modes.creative.storage.blockvolume.iterator.VolumeIterator;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

public class BlockEmpty implements BlockVolume {
    @Override
    public @Nullable Block getBlock(int x, int y, int z) {
        return null;
    }

    @Override
    public VolumeIterator iterator() {
        return EMPTY_ITERATOR;
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public BoundingBox toCuboid() {
        return new BoundingBox(0, 0, 0);
    }

    private static final VolumeIterator EMPTY_ITERATOR = new VolumeIterator() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public void next(BlockConsumer consumer) {

        }
    };
}
