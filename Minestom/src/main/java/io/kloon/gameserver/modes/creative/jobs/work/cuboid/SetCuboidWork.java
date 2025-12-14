package io.kloon.gameserver.modes.creative.jobs.work.cuboid;

import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.builtin.SetCuboidChange;
import io.kloon.gameserver.modes.creative.jobs.work.CuboidWork;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class SetCuboidWork extends CuboidWork {
    private final Block fillBlock;

    public SetCuboidWork(CreativeInstance instance, BoundingBox cuboid, Block fillBlock) {
        super(instance, cuboid);
        this.fillBlock = fillBlock;
    }

    @Override
    protected Block compute(Instance instance, Point blockPos, Block existing) {
        return fillBlock;
    }

    @Override
    public Change getChange() {
        return new SetCuboidChange(before.build(), fillBlock);
    }
}
