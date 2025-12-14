package io.kloon.gameserver.modes.creative.jobs.work;

import io.kloon.gameserver.minestom.blockchange.MultiBlockChange;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeBuilder;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.function.BooleanSupplier;

public abstract class CuboidWork implements BlocksWork {
    protected final CreativeInstance instance;
    protected final BoundingBox cuboid;

    private int dx = 0;
    private int dy = 0;
    private int dz = 0;

    private int iterations = 0;
    private boolean outOfBounds = false;

    protected final BlockVolumeBuilder before = new BlockVolumeBuilder();

    public CuboidWork(CreativeInstance instance, BoundingBox cuboid) {
        this.instance = instance;
        this.cuboid = cuboid;
    }

    @Override
    public final boolean work(BooleanSupplier greenFlag) {
        MultiBlockChange multi = new MultiBlockChange(instance);

        try {
            for (; dy < cuboid.height(); ++dy) {
                for (; dz < cuboid.depth(); ++dz) {
                    for (; dx < cuboid.width(); ++dx) {
                        if (!greenFlag.getAsBoolean()) return false;

                        ++iterations;

                        Point blockPos = cuboid.relativeStart().add(dx, dy, dz);
                        if (instance.isOutOfBounds(blockPos)) {
                            outOfBounds = true;
                            continue;
                        }

                        Block blockBefore = instance.getBlock(blockPos);

                        Block computed = compute(instance, blockPos, blockBefore);

                        if (computed != blockBefore || computed.hasNbt()) {
                            before.set(blockPos, blockBefore);
                            onChange(instance, blockPos, blockBefore, computed);
                            multi.set(blockPos, computed);
                        }
                    }
                    dx = 0;
                }
                dz = 0;
            }
            return true;
        } finally {
            multi.applyAndBroadcast();
        }
    }

    protected abstract Block compute(Instance instance, Point blockPos, Block existing);

    protected void onChange(Instance instance, Point blockPos, Block before, Block after) {

    }

    @Override
    public final int getPlacedSoFar() {
        return iterations;
    }

    @Override
    public final int getTotalToPlace() {
        return (int) BoundingBoxUtils.volumeRounded(cuboid);
    }

    @Override
    public final BoundingBox getBoundingBox() {
        return cuboid;
    }

    @Override
    public boolean hadOutOfBounds() {
        return outOfBounds;
    }
}
