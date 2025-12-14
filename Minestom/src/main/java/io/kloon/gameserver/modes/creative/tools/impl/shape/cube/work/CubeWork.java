package io.kloon.gameserver.modes.creative.tools.impl.shape.cube.work;

import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.builtin.ApplyVolumeChange;
import io.kloon.gameserver.modes.creative.jobs.work.CuboidWork;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeBuilder;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class CubeWork extends CuboidWork {
    private final CubeGenSettings settings;

    private final BlockVolumeBuilder after = new BlockVolumeBuilder();

    public CubeWork(CreativeInstance instance, CubeGenSettings settings) {
        super(instance, settings.boundingBox());
        this.settings = settings;
    }

    @Override
    protected Block compute(Instance instance, Point blockPos, Block existing) {
        BoundingBox boundingBox = settings.boundingBox();

        if (settings.hollow() && !isEdge(boundingBox, blockPos)) {
            return existing;
        }

        if (settings.mask().isIgnored(instance, blockPos, existing)) {
            return existing;
        }

        return settings.pattern().computeBlock(instance, blockPos);
    }

    private boolean isEdge(BoundingBox boundingBox, Point blockPos) {
        Vec diff = Vec.fromPoint(blockPos.sub(boundingBox.relativeStart()));
        return diff.x() == 0 || diff.x() == boundingBox.width() - 1
                || diff.y() == 0 || diff.y() == boundingBox.height() - 1
                || diff.z() == 0 || diff.z() == boundingBox.depth() - 1;
    }

    @Override
    protected void onChange(Instance instance, Point blockPos, Block before, Block after) {
        this.after.set(blockPos, after);
    }

    @Override
    public Change getChange() {
        return new ApplyVolumeChange(before.build(), after.build());
    }
}
