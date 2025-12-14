package io.kloon.gameserver.modes.creative.jobs.work.cuboid;

import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.builtin.ApplyVolumeChange;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.jobs.work.CuboidWork;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeBuilder;
import io.kloon.gameserver.util.physics.Collisions;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class SetCuboidExceptWork extends CuboidWork {
    private final Block fillBlock;
    private final BlocksWork exceptWork;

    private final BlockVolumeBuilder after = new BlockVolumeBuilder();

    public SetCuboidExceptWork(CreativeInstance instance, BoundingBox cuboid, Block fillBlock, BlocksWork exceptWork) {
        super(instance, cuboid);
        this.fillBlock = fillBlock;
        this.exceptWork = exceptWork;
    }

    @Override
    protected Block compute(Instance instance, Point blockPos, Block existing) {
        BoundingBox exceptBoundingBox = exceptWork.getBoundingBox();
        if (Collisions.containsExclusive(exceptBoundingBox, blockPos)) {
            return existing;
        }

        return fillBlock;
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
