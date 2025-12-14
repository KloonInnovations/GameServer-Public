package io.kloon.gameserver.modes.creative.tools.impl.fill.work;

import io.kloon.gameserver.minestom.blockchange.MultiBlockChange;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.builtin.ApplyVolumeChange;
import io.kloon.gameserver.modes.creative.jobs.BlocksJob;
import io.kloon.gameserver.modes.creative.jobs.BlocksJobQueue;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeBuilder;
import io.kloon.gameserver.util.coordinates.CardinalDirection;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class FloodFillBlock implements BlocksWork {
    private final CreativeInstance instance;
    private final Block mustMatch;
    private final CreativePattern pattern;
    private final MaskLookup mask;

    private final Queue<Point> toCheck = new LinkedList<>();
    private final Set<Point> checked = new HashSet<>();

    private final BlockVolumeBuilder before = new BlockVolumeBuilder();
    private final BlockVolumeBuilder after = new BlockVolumeBuilder();

    public static final int MAX_FILL = 110_592; // 48^3

    public FloodFillBlock(CreativeInstance instance, Point initial, CreativePattern pattern, MaskLookup mask) {
        this.instance = instance;
        this.pattern = pattern;
        this.mask = mask;
        this.mustMatch = instance.getBlock(initial);
        toCheck.add(initial);
        mask.setIgnoreBlockMatch(true);
    }

    @Override
    public boolean work(BooleanSupplier greenFlag) {
        MultiBlockChange multi = new MultiBlockChange(instance);
        try {
            BlocksJobQueue jobQueue = instance.getJobQueue();

            while (!toCheck.isEmpty()) {
                if (!greenFlag.getAsBoolean()) {
                    return false;
                }

                Point blockPos = toCheck.poll();
                Block block = instance.getBlock(blockPos);
                if (!mustMatch.equals(block)) continue;

                BlocksJob job = jobQueue.getJob(blockPos);
                if (job != null && job.getWork() != this) continue;

                if (instance.isOutOfBounds(blockPos)) continue;

                for (BlockFace face : CardinalDirection.FACES) {
                    Point relPos = blockPos.relative(face);
                    if (checked.add(relPos)) {
                        toCheck.add(relPos);
                    }
                }

                if (mask.isIgnored(instance, blockPos, block)) {
                    continue;
                }

                before.set(blockPos, block);

                Block computed = pattern.computeBlock(instance, blockPos);
                after.set(blockPos, computed);
                multi.set(blockPos, computed);

                if (checked.size() >= MAX_FILL) {
                    return true;
                }
            }

            return true;
        } finally {
            multi.applyAndBroadcast();
        }
    }

    @Override
    public int getPlacedSoFar() {
        return checked.size();
    }

    @Override
    public int getTotalToPlace() {
        return checked.size() + toCheck.size();
    }

    @Override
    public BoundingBox getBoundingBox() {
        return after.getCuboid();
    }

    @Override
    public Change getChange() {
        return new ApplyVolumeChange(before.build(), after.build());
    }
}
