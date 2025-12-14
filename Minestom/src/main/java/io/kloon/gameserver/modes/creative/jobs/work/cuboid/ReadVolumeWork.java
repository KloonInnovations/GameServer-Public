package io.kloon.gameserver.modes.creative.jobs.work.cuboid;

import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.builtin.ReadVolumeChange;
import io.kloon.gameserver.modes.creative.jobs.work.CuboidWork;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolume;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeBuilder;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class ReadVolumeWork extends CuboidWork {
    private final BlockVolumeBuilder builder = new BlockVolumeBuilder();

    public ReadVolumeWork(CreativeInstance instance, BoundingBox cuboid) {
        super(instance, cuboid);
    }

    @Override
    protected Block compute(Instance instance, Point blockPos, Block existing) {
        builder.set(blockPos, existing);
        return existing;
    }

    @Override
    public Change getChange() {
        BlockVolume volume = builder.build();
        return new ReadVolumeChange(volume);
    }
}
