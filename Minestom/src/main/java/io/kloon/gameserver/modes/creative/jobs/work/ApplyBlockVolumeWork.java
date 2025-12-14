package io.kloon.gameserver.modes.creative.jobs.work;

import io.kloon.gameserver.minestom.blockchange.MultiBlockChange;
import io.kloon.gameserver.modes.creative.history.Change;
import io.kloon.gameserver.modes.creative.history.builtin.ApplyVolumeChange;
import io.kloon.gameserver.modes.creative.jobs.BlocksWork;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolume;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeBuilder;
import io.kloon.gameserver.modes.creative.storage.blockvolume.iterator.VolumeIterator;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.instance.Instance;

import java.util.function.BooleanSupplier;

public class ApplyBlockVolumeWork implements BlocksWork {
    private final Instance instance;
    private final BlockVolume volume;

    private final VolumeIterator iterator;

    private final BlockVolumeBuilder before = new BlockVolumeBuilder();

    public ApplyBlockVolumeWork(Instance instance, BlockVolume volume) {
        this.instance = instance;
        this.volume = volume;

        this.iterator = volume.iterator();
    }

    @Override
    public boolean work(BooleanSupplier greenFlag) {
        MultiBlockChange multi = new MultiBlockChange(instance);
        try {
            while (iterator.hasNext()) {
                if (!greenFlag.getAsBoolean()) return false;
                iterator.next((x, y, z, block) -> {
                    before.set(x, y, z, instance);
                    multi.set(x, y, z, block);
                });
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
        return volume.count();
    }

    @Override
    public BoundingBox getBoundingBox() {
        return volume.toCuboid();
    }

    @Override
    public Change getChange() {
        return new ApplyVolumeChange(before.build(), volume);
    }
}
