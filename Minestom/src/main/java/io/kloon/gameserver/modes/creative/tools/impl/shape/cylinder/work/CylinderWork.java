package io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.work;

import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.builtin.ApplyVolumeChange;
import io.kloon.gameserver.modes.creative.jobs.work.CuboidWork;
import io.kloon.gameserver.modes.creative.selection.rendering.DisplayCuboid;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeBuilder;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.MathUtils;

import java.util.Arrays;
import java.util.List;

public class CylinderWork extends CuboidWork {
    private final CylinderGenSettings settings;
    private final BlockVolumeBuilder after = new BlockVolumeBuilder();

    private final Point center;

    private final double radius;
    private final double radiusSq;

    private static final List<Vec> HOLLOW_OFFSETS = Arrays.asList(new Vec(1, 0, 0), new Vec(0, 1, 0), new Vec(0, 0, 1));

    public CylinderWork(CreativeInstance instance, CylinderGenSettings settings) {
        super(instance, settings.generateBoundingBox());
        this.settings = settings;

        Point center = settings.center();
        double radius = settings.radius();

        if (settings.odd()) {
            center = center.add(0.5, 0.5, 0.5);
            radius += 0.5;
        }

        this.center = center;
        this.radius = radius;
        this.radiusSq = radius * radius;
    }

    @Override
    protected Block compute(Instance instance, Point blockPos, Block existing) {
        if (!isInCylinder(blockPos)) {
            return existing;
        }

        int blockY = blockPos.blockY();
        if (settings.hollow() && !(blockY == (int) cuboid.minY() || blockY == (int) cuboid.maxY() - 1)) {
            Vec dist = Vec.fromPoint(blockPos.sub(center));
            Vec distAbs = new Vec(
                    Math.abs(dist.x()),
                    Math.abs(dist.y()),
                    Math.abs(dist.z()));

            if (HOLLOW_OFFSETS.stream().allMatch(offset -> isInCylinder(distAbs.add(center).add(offset)))) {
                return existing;
            }
        }

        if (settings.mask().isIgnored(instance, blockPos, existing)) {
            return existing;
        }

        return settings.pattern().computeBlock(instance, blockPos);
    }

    private boolean isInCylinder(Point blockPos) {
        double distXZSq = MathUtils.square(blockPos.x() - center.x()) + MathUtils.square(blockPos.z() - center.z());
        return distXZSq <= radiusSq;
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
