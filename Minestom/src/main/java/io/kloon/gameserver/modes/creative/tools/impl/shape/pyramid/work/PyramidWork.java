package io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.work;

import io.kloon.gameserver.minestom.blockchange.MultiBlockChange;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.builtin.ApplyVolumeChange;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeBuilder;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.function.BooleanSupplier;

public class PyramidWork implements BlocksWork {
    protected final CreativeInstance instance;
    protected final PyramidGenSettings settings;
    protected final BoundingBox boundingBox;

    private int cellX = 0;
    private int cellY = 0;
    private int cellZ = 0;
    private int mirrorIndex = 0;

    protected final BlockVolumeBuilder before = new BlockVolumeBuilder();
    protected final BlockVolumeBuilder after = new BlockVolumeBuilder();

    private boolean outOfBounds = false;

    private static final Vec[] MIRRORS = {
            new Vec(1, 1, 1),
            new Vec(1, 1, -1),
            new Vec(-1, 1, 1),
            new Vec(-1, 1, -1)
    };

    public PyramidWork(CreativeInstance instance, PyramidGenSettings settings) {
        this.instance = instance;
        this.settings = settings;
        this.boundingBox = settings.computeBoundingBox();
    }

    @Override
    public boolean work(BooleanSupplier greenFlag) {
        MultiBlockChange multi = new MultiBlockChange(instance);

        int maxHeight = settings.steps() * settings.stepHeight();

        try {
            Vec cellSize = new Vec(settings.stepLength(), settings.stepHeight(), settings.stepLength());
            Point center = settings.bottomCenter().sub(cellSize.mul(0.5, 0, 0.5).apply(Vec.Operator.FLOOR));

            for (; cellY < settings.steps(); ++cellY) {
                int length = settings.steps() - cellY;
                for (; mirrorIndex < MIRRORS.length; ++mirrorIndex) {
                    Vec mirror = MIRRORS[mirrorIndex];
                    for (; cellZ < length; ++cellZ) {
                        for (; cellX < length; ++cellX) {
                            if (!greenFlag.getAsBoolean()) return false;

                            if (settings.hollow() && !(cellX == length - 1 || cellZ == length - 1)) {
                                continue;
                            }

                            Vec cellOffset = cellSize.mul(cellX, cellY, cellZ).mul(mirror);

                            for (int dy = 0; dy < settings.stepHeight(); ++dy) {
                                for (int dx = 0; dx < settings.stepLength(); ++dx) {
                                    for (int dz = 0; dz < settings.stepLength(); ++dz) {
                                        Point blockPos = center.add(cellOffset).add(dx, dy, dz);
                                        if (settings.upsideDown()) {
                                            int relY = blockPos.blockY() - center.blockY();
                                            blockPos = blockPos.withY(center.blockY() + maxHeight - 1 - relY);
                                        }

                                        Block blockBefore = instance.getBlock(blockPos);

                                        if (settings.mask().isIgnored(instance, blockPos, blockBefore)) {
                                            continue;
                                        }

                                        Block computed = settings.pattern().computeBlock(instance, blockPos);

                                        if (computed == blockBefore) continue;
                                        if (instance.isOutOfBounds(blockPos)) {
                                            outOfBounds = true;
                                            continue;
                                        }

                                        before.set(blockPos, blockBefore);

                                        after.set(blockPos, computed);
                                        multi.set(blockPos, computed);
                                    }
                                }
                            }
                        }
                        cellX = 0;
                    }
                    cellZ = 0;
                }
                mirrorIndex = 0;
            }
            return true;
        } finally {
            multi.applyAndBroadcast();
        }
    }

    @Override
    public int getPlacedSoFar() {
        return before.countBlocks();
    }

    @Override
    public int getTotalToPlace() {
        return (int) BoundingBoxUtils.volumeRounded(boundingBox) / 4;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    @Override
    public boolean hadOutOfBounds() {
        return outOfBounds;
    }

    @Override
    public Change getChange() {
        return new ApplyVolumeChange(before.build(), after.build());
    }
}
