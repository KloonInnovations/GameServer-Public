package io.kloon.gameserver.modes.creative.storage.blockvolume.iterator;

// iterates over recorded blocks in the volume
public interface VolumeIterator {
    boolean hasNext();

    void next(BlockConsumer consumer);
}
