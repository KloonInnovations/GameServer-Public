package io.kloon.gameserver.modes.creative.tools.impl.layer.work;

import io.kloon.gameserver.modes.creative.history.builtin.ApplyVolumeChange;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolume;
import io.kloon.gameserver.util.coordinates.Axis;

public class LayerChange extends ApplyVolumeChange {
    private final Axis axis;

    public LayerChange(BlockVolume before, BlockVolume after, Axis axis) {
        super(before, after);
        this.axis = axis;
    }

    public Axis getAxis() {
        return axis;
    }
}
