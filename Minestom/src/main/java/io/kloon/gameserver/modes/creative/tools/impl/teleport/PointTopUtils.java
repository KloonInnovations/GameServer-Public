package io.kloon.gameserver.modes.creative.tools.impl.teleport;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

public final class PointTopUtils {
    private PointTopUtils() {}

    @Nullable
    public static BlockVec findFloorOfRoof(Instance instance, Point point) {
        int maxY = instance.getCachedDimensionType().maxY();
        for (int y = point.blockY() + 1; y < maxY; ++y) {
            Block block = instance.getBlock(point.withY(y));
            Block below = instance.getBlock(point.withY(y - 1));

            if (!block.isSolid() && below.isSolid()) {
                return new BlockVec(point.withY(y - 1));
            }
        }
        return null;
    }

    @Nullable
    public static BlockVec findFloorOfBasement(Instance instance, Point point) {
        int minY = instance.getCachedDimensionType().minY();
        for (int y = point.blockY() - 1; y >= minY; --y) {
            Block block = instance.getBlock(point.withY(y));
            Block below = instance.getBlock(point.withY(y - 1));

            if (!block.registry().isSolid() && below.registry().isSolid()) {
                return new BlockVec(point.withY(y - 1));
            }
        }
        return null;
    }
}
