package io.kloon.gameserver.modes.creative.tools.impl.laser.work;

import io.kloon.gameserver.minestom.blockchange.MultiBlockChange;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.builtin.ApplyVolumeChange;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeBuilder;
import io.kloon.gameserver.modes.creative.tools.impl.laser.mode.LaserMode;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.block.BlockIterator;

import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

public class LaserWork implements BlocksWork {
    private final CreativeInstance instance;
    private final LaserGenSettings settings;

    private final int length;
    private final LaserMode mode;
    private final Vec dir;

    private final BlockIterator iterator;
    private int lastDist = 0;

    private final BlockVolumeBuilder before = new BlockVolumeBuilder();
    private final BlockVolumeBuilder after = new BlockVolumeBuilder();

    public LaserWork(CreativeInstance instance, LaserGenSettings settings) {
        this.instance = instance;
        this.settings = settings;

        Vec start = settings.eyePos();
        Vec end = settings.end();

        this.dir = end.sub(start).normalize();
        this.length = (int) Math.ceil(start.distance(end));

        this.mode = settings.mode().create();

        double offset = Math.min(length - 1, settings.offset());
        start = start.add(dir.mul(offset));

        iterator = new BlockIterator(start, dir, 0, length - offset, true);
    }

    @Override
    public boolean work(BooleanSupplier greenFlag) {
        MultiBlockChange multi = new MultiBlockChange(instance);

        try {
            while (iterator.hasNext()) {
                if (!greenFlag.getAsBoolean()) {
                    return false;
                }

                Point center = iterator.next();
                lastDist = (int) Math.floor(center.distance(settings.eyePos()));

                List<BlockVec> around;
                if (settings.radius() == 0) {
                    around = Collections.singletonList(new BlockVec(center));
                } else {
                    around = mode.getBlocksAround(center, dir, settings.radius());
                }

                for (BlockVec blockPos : around) {
                    Chunk chunk = instance.getChunkAt(blockPos);
                    if (chunk == null || !chunk.isLoaded()) continue;

                    Block existing = instance.getBlock(blockPos);
                    if (!settings.ignoreBlocks() && !existing.isAir()) {
                        continue;
                    }

                    if (settings.mask().isIgnored(instance, blockPos, existing)) {
                        continue;
                    }

                    before.set(blockPos, existing);

                    Block computed = settings.pattern().computeBlock(instance, blockPos);
                    if (computed != existing) {
                        after.set(blockPos, computed);
                        multi.set(blockPos, computed);
                    }
                }
            }
            return true;
        } finally {
            multi.applyAndBroadcast();
        }
    }

    @Override
    public int getPlacedSoFar() {
        return lastDist;
    }

    @Override
    public int getTotalToPlace() {
        return length;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return new BoundingBox(1, 1, 1, settings.eyePos());
    }

    @Override
    public Change getChange() {
        return new ApplyVolumeChange(before.build(), after.build());
    }
}
