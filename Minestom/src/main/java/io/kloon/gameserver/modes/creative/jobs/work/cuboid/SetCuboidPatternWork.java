package io.kloon.gameserver.modes.creative.jobs.work.cuboid;

import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.builtin.ApplyVolumeChange;
import io.kloon.gameserver.modes.creative.jobs.work.CuboidWork;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeBuilder;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetCuboidPatternWork extends CuboidWork {
    private static final Logger LOG = LoggerFactory.getLogger(SetCuboidPatternWork.class);

    private final CreativePattern pattern;
    private final MaskLookup mask;

    private final BlockVolumeBuilder after = new BlockVolumeBuilder();

    public SetCuboidPatternWork(CreativeInstance instance, BoundingBox cuboid, CreativePattern pattern, MaskLookup mask) {
        super(instance, cuboid);
        this.pattern = pattern;
        this.mask = mask;
    }

    @Override
    protected Block compute(Instance instance, Point blockPos, Block existing) {
        if (mask.isIgnored(instance, blockPos, existing)) {
            return existing;
        }

        Block block = pattern.computeBlock(instance, blockPos);
        after.set(blockPos, block);
        return block;
    }

    @Override
    public Change getChange() {
        return new ApplyVolumeChange(before.build(), after.build());
    }
}
