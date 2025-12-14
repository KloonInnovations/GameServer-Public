package io.kloon.gameserver.modes.creative.tools.impl.blend.work;

import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.jobs.work.cuboid.CuboidVolumeWork;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BlendWork extends CuboidVolumeWork {
    private final BlendGen blendGen;

    private final double radiusSq;

    public BlendWork(CreativeInstance instance, BlendGen blendGen) {
        super(instance, blendGen.boundingBox());
        this.blendGen = blendGen;
        this.radiusSq = blendGen.radiusSquared();
    }

    @Override
    protected Block compute(Instance instance, Point blockPos, Block existing) {
        if (blendGen.sphere() && blockPos.distanceSquared(blendGen.center()) > radiusSq) {
            return existing;
        }

        if (blendGen.mask().isIgnored(instance, blockPos, existing)) {
            return existing;
        }

        Vec[] offsets = blendGen.sampling().getOffsets();
        return computeBlend(instance, blockPos, offsets);
    }

    private Block computeBlend(Instance instance, Point blockPos, Vec[] offsets) {
        Block existing = instance.getBlock(blockPos);
        Map<Block, Integer> frequencies = new HashMap<>(offsets.length);
        for (Vec offset : offsets) {
            Point relPos = blockPos.add(offset);
            Block relBlock = instance.getBlock(relPos);
            if (relBlock.isAir() && blendGen.doNotSampleAir()) continue;
            if (relBlock.isLiquid() && blendGen.doNotSampleLiquid()) continue;
            frequencies.compute(relBlock, (_, prev) -> prev == null ? 1 : prev + 1);
        }

        List<BlockAndFrequency> list = frequencies.entrySet().stream()
                .map(e -> new BlockAndFrequency(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingInt(f -> f.frequency))
                .toList();
        if (list.isEmpty()) {
            return existing;
        }
        if (list.size() == 1) {
            return list.getFirst().block();
        }

        BlockAndFrequency mostFrequent = list.get(list.size() - 1);
        BlockAndFrequency lastButOne = list.get(list.size() - 2);
        if (mostFrequent.frequency == lastButOne.frequency) {
            if (blendGen.doNotChangeOnTies()) {
                return existing;
            } else {
                ThreadLocalRandom rand = ThreadLocalRandom.current();
                return (rand.nextBoolean() ? mostFrequent : lastButOne).block;
            }
        }
        return mostFrequent.block;
    }

    private record BlockAndFrequency(Block block, int frequency) {}
}
