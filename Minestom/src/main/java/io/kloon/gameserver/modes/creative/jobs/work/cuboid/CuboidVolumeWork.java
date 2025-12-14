package io.kloon.gameserver.modes.creative.jobs.work.cuboid;

import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.builtin.ApplyVolumeChange;
import io.kloon.gameserver.modes.creative.jobs.work.CuboidWork;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeBuilder;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public abstract class CuboidVolumeWork extends CuboidWork {
    protected final BlockVolumeBuilder after = new BlockVolumeBuilder();

    public CuboidVolumeWork(CreativeInstance instance, BoundingBox cuboid) {
        super(instance, cuboid);
    }

    @Override
    protected final void onChange(Instance instance, Point blockPos, Block before, Block after) {
        super.onChange(instance, blockPos, before, after);
        this.after.set(blockPos, after);
    }

    @Override
    public Change getChange() {
        return new ApplyVolumeChange(before.build(), after.build());
    }
}
