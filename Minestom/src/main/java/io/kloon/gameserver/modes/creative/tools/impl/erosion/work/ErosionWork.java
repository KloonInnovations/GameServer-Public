package io.kloon.gameserver.modes.creative.tools.impl.erosion.work;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.jobs.work.cuboid.CuboidVolumeWork;
import io.kloon.gameserver.modes.creative.jobs.work.cuboid.SnapshottedWork;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolumeBuilder;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.params.ErosionParams;
import io.kloon.gameserver.util.coordinates.CardinalDirection;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;

import java.util.*;
import java.util.stream.Stream;

public class ErosionWork extends SnapshottedWork {
    private final ErosionGen erosionGen;
    private final ErosionParams params;

    private final double radiusSq;

    public ErosionWork(CreativeInstance instance, ErosionGen genSettings) {
        super(instance, genSettings.boundingBox(), computeSnapshots(genSettings));
        this.erosionGen = genSettings;
        this.params = erosionGen.params();
        this.radiusSq = erosionGen.radiusSquared();
    }

    @Override
    protected Block computeSnapshot(Block.Getter before, Point blockPos, int snapshotIndex) {
        Block existing = before.getBlock(blockPos);
        if (snapshotIndex == 0) {
            return existing;
        }

        if (erosionGen.sphere() && blockPos.distanceSquared(erosionGen.center()) > radiusSq) {
            return existing;
        }

        boolean erode = (snapshotIndex - 1) < params.erosionIterations();
        if (erode) {
            return iterate(before, blockPos, true);
        }

        return iterate(before, blockPos, false);
    }

    private Block iterate(Block.Getter instance, Point blockPos, boolean erode) {
        Block existing = instance.getBlock(blockPos);

        if (erosionGen.mask().isIgnored(instance, blockPos, existing)) {
            return existing;
        }

        boolean existingSolid = existing.isSolid();
        if (erode == !existingSolid) {
            return existing;
        }

        Map<Block, Integer> frequencies = fetchAdjacentBlocks(instance, blockPos, erode);
        int matchingAdjacent = frequencies.values().stream().mapToInt(e -> e).sum();
        int facesToMatch = erode ? params.erosionFaces() : params.fillFaces();
        if (matchingAdjacent >= facesToMatch) {
            return getMostFrequent(frequencies, existing);
        }

        return existing;
    }

    private Map<Block, Integer> fetchAdjacentBlocks(Block.Getter instance, Point blockPos, boolean erode) {
        Map<Block, Integer> frequencies = new HashMap<>();
        for (BlockFace face : CardinalDirection.FACES) {
            Point relPos = blockPos.relative(face);
            Block relBlock = instance.getBlock(relPos);
            boolean relSolid = relBlock.isSolid();
            if (erode == relSolid) {
                continue;
            }
            frequencies.compute(relBlock, (_, prev) -> prev == null ? 1 : prev + 1);
        }
        return frequencies;
    }

    private <T> T getMostFrequent(Map<T, Integer> counts, T def) {
        return counts.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey).orElse(def);
    }

    private static int computeSnapshots(ErosionGen gen) {
        ErosionParams params = gen.params();
        return params.erosionIterations() + params.fillIterations() + 1;
    }
}
