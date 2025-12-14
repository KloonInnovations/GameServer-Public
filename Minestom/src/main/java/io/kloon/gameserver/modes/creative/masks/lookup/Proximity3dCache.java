package io.kloon.gameserver.modes.creative.masks.lookup;

import io.kloon.gameserver.modes.creative.masks.impl.proximity.util.ManhattanIteration;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Proximity3dCache { // TODO: do caching...
    private final Map<BlockVec, ComputedBlock> cache = new HashMap<>();

    private int getBlocks = 0;
    private int computes = 0;

    // returns MAX if not found
    public double computeNearestNonAirWithinSq(Block.Getter instance, Point to, int manhattanRange) {
        ComputedBlock computed = computeNearestNonAir(instance, new BlockVec(to), Math.max(8, manhattanRange));
        //Kgs.broadcastMessage("get " + getBlocks + " compute " + computes);
        if (computed.nearest() == null) {
            return Double.MAX_VALUE;
        }
        return computed.nearest().distanceSquared(to);
    }

    private ComputedBlock computeNearestNonAir(Block.Getter instance, BlockVec blockPos, int manhattanRange) {
        ++computes;
        AtomicReference<ComputedBlock> nearest = new AtomicReference<>(new ComputedBlock(blockPos, null, manhattanRange, Integer.MAX_VALUE));
        ManhattanIteration.iterate(blockPos, manhattanRange * 2 + 1, relPos -> {
            Block block = instance.getBlock(relPos);
            ++getBlocks;
            int range = ManhattanIteration.manhanttanDistance(blockPos, relPos);
            if (!block.isAir()) {
                nearest.set(new ComputedBlock(blockPos, new BlockVec(relPos), manhattanRange, range));
                return false;
            }
            return true;
        });
        return nearest.get();
    }
    private record ComputedBlock(BlockVec from, @Nullable BlockVec nearest, int checkedRange, int distToNotAir) {}
}
