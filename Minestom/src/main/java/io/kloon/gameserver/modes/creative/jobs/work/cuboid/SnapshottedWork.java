package io.kloon.gameserver.modes.creative.jobs.work.cuboid;

import io.kloon.gameserver.minestom.blockchange.MultiBlockChange;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.builtin.ApplyVolumeChange;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolume;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeBuilder;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public abstract class SnapshottedWork implements BlocksWork {
    private final CreativeInstance instance;
    protected final BoundingBox cuboid;

    private int snapshotIndex = 0;
    private int dx = 0;
    private int dy = 0;
    private int dz = 0;

    private int blockIterations = 0;
    private boolean outOfBounds = false;

    private final BlockVolumeBuilder before = new BlockVolumeBuilder();
    private final List<Snapshot> snapshots = new ArrayList<>();

    public SnapshottedWork(CreativeInstance instance, BoundingBox cuboid, int snapshots) {
        this.instance = instance;
        this.cuboid = cuboid;
        for (int i = 0; i < snapshots; ++i) {
            this.snapshots.add(new Snapshot(i, new BlockVolumeBuilder()));
        }
    }

    @Override
    public final boolean work(BooleanSupplier greenFlag) {
        for (; snapshotIndex < snapshots.size(); ++snapshotIndex) {
            Snapshot snapshot = snapshots.get(snapshotIndex);
            Block.Getter getter = snapshotIndex == 0
                    ? instance
                    : snapshots.get(snapshotIndex - 1).after().getter(instance);
            for (; dy < cuboid.height(); ++dy) {
                for (; dz < cuboid.depth(); ++dz) {
                    for (; dx < cuboid.width(); ++dx) {
                        if (!greenFlag.getAsBoolean()) return false;

                        ++blockIterations;

                        Point blockPos = cuboid.relativeStart().add(dx, dy, dz);
                        if (instance.isOutOfBounds(blockPos)) {
                            outOfBounds = true;
                            continue;
                        }

                        Block blockBefore = instance.getBlock(blockPos);
                        if (snapshotIndex == 0) {
                            before.set(blockPos, blockBefore);
                        }

                        Block computed = computeSnapshot(getter, blockPos, snapshotIndex);
                        snapshot.after.set(blockPos, computed);
                    }
                    dx = 0;
                }
                dz = 0;
            }
            dy = 0;
        }

        MultiBlockChange multi = new MultiBlockChange(instance);

        try {
            BlockVolumeBuilder last = snapshots.getLast().after();
            for (; dy < cuboid.height(); ++dy) {
                for (; dz < cuboid.depth(); ++dz) {
                    for (; dx < cuboid.width(); ++dx) {
                        if (!greenFlag.getAsBoolean()) return false;

                        ++blockIterations;

                        Point blockPos = cuboid.relativeStart().add(dx, dy, dz);
                        if (instance.isOutOfBounds(blockPos)) {
                            outOfBounds = true;
                            continue;
                        }

                        Block block = last.get(blockPos);
                        multi.set(blockPos, block);
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

    protected abstract Block computeSnapshot(Block.Getter before, Point blockPos, int snapshotIndex);

    @Override
    public int getPlacedSoFar() {
        return blockIterations;
    }

    @Override
    public int getTotalToPlace() {
        int volume = (int) BoundingBoxUtils.volumeRounded(cuboid);
        return volume * (snapshots.size() + 1);
    }

    @Override
    public BoundingBox getBoundingBox() {
        return cuboid;
    }

    @Override
    public Change getChange() {
        BlockVolume beforeVol = before.build();
        BlockVolume afterVol = snapshots.getLast().after.build();
        return new ApplyVolumeChange(beforeVol, afterVol);
    }

    @Override
    public boolean hadOutOfBounds() {
        return outOfBounds;
    }

    private record Snapshot(int index, BlockVolumeBuilder after) {}
}
