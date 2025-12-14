package io.kloon.gameserver.modes.creative.tools.impl.layer.work;

import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.jobs.work.cuboid.CuboidVolumeWork;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class LayerWork extends CuboidVolumeWork {
    private final LayerGen layerGen;

    private final double radiusSq;

    public LayerWork(CreativeInstance instance, LayerGen layerGen) {
        super(instance, layerGen.computeBoundingBox());
        this.layerGen = layerGen;
        this.radiusSq = layerGen.radiusSq();
    }

    @Override
    protected Block compute(Instance instance, Point blockPos, Block existing) {
        if (!isInShape(blockPos)) {
            return existing;
        }

        if (layerGen.mask().isIgnored(instance, blockPos, existing)) {
            return existing;
        }

        return layerGen.pattern().computeBlock(instance, blockPos);
    }

    private boolean isInShape(Point blockPos) {
        return switch (layerGen.shape()) {
            case SQUARE -> true;
            case CIRCLE -> {
                double distSq = blockPos.distanceSquared(layerGen.center());
                yield distSq <= radiusSq;
            }
        };
    }

    @Override
    public Change getChange() {
        return new LayerChange(before.build(), after.build(), layerGen.axis());
    }
}
