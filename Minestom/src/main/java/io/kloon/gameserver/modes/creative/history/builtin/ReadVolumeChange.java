package io.kloon.gameserver.modes.creative.history.builtin;

import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolume;

public class ReadVolumeChange extends NoChange {
    private final BlockVolume volume;

    public ReadVolumeChange(BlockVolume volume) {
        this.volume = volume;
    }

    public BlockVolume getVolume() {
        return volume;
    }
}
