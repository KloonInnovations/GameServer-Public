package io.kloon.gameserver.modes.creative.jobs.work.cuboid;

import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.builtin.ApplyVolumeChange;
import io.kloon.gameserver.modes.creative.jobs.work.CuboidWork;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeBuilder;
import io.kloon.gameserver.modes.creative.tools.impl.replace.replacementconfig.ReplacementConfig;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class ReplaceWork extends CuboidWork {
    private final ReplacementConfig replacementConfig;

    private final BlockVolumeBuilder after = new BlockVolumeBuilder();

    public ReplaceWork(CreativeInstance instance, BoundingBox cuboid, ReplacementConfig replacementConfig) {
        super(instance, cuboid);
        this.replacementConfig = replacementConfig;
    }

    @Override
    protected Block compute(Instance instance, Point blockPos, Block existing) {
        CreativePattern replacement = replacementConfig.get(existing);
        if (replacement == null) {
            return existing;
        }

        return replacement.computeBlock(instance, blockPos);
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
