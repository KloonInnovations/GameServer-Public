package io.kloon.gameserver.modes.creative.masks.impl.proximity.util;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

// https://stackoverflow.com/a/37287209
public class ManhattanIteration {
    public static void iterate(Point center, int manhattanDiameter, Function<Point, Boolean> consumer) {
        if (manhattanDiameter <= 0) {
            return;
        }

        Point centerOffset = center.sub(new Vec(Math.floor(manhattanDiameter / 2.0)));
        AtomicBoolean keepGoing = new AtomicBoolean(true);
        OffsetConsumer offsetConsumer = (x, y, z) -> {
            boolean ok = consumer.apply(centerOffset.add(x, y, z));
            keepGoing.set(ok);
            return ok;
        };

        int half = (int) Math.ceil((double) manhattanDiameter / 2) - 1;
        for (int d = 0; d <= 3 * half; ++d) {
            for (int x = Math.max(0, d - 2 * half); x <= Math.min(half, d); ++x) {
                for (int y = Math.max(0, d - x - half); y <= Math.min(half, d - x); ++y) {
                    if (manhattanDiameter % 2 == 0) {
                        mirrorEven(half, x, y, d - x - y, offsetConsumer);
                    } else {
                        mirrorOdd(half, x, y, d - x - y, offsetConsumer);
                    }
                    if (!keepGoing.get()) {
                        return;
                    }
                }
            }
        }
    }

    private static void mirrorEven(int half, int x, int y, int z, OffsetConsumer consumer) {
        consumer.accept(half + x + 1, half + y + 1, half + z + 1);
        consumer.accept(half + x + 1, half + y + 1, half - z);
        consumer.accept(half + x + 1, half - y, half + z + 1);
        consumer.accept(half + x + 1, half - y, half - z);
        consumer.accept(half - x, half + y + 1, half + z + 1);
        consumer.accept(half - x, half + y + 1, half - z);
        consumer.accept(half - x, half - y, half + z + 1);
        consumer.accept(half - x, half - y, half - z);
    }

    private static void mirrorOdd(int half, int x, int y, int z, OffsetConsumer consumer) {
        for (var i = 0; i < (x != 0 ? 2 : 1); ++i, x *= -1) {
            for (var j = 0; j < (y != 0? 2 : 1); ++j, y *= -1) {
                for (var k = 0; k < (z != 0 ? 2 : 1); ++k, z *= -1) {
                    boolean keepGoing = consumer.accept(half + x, half + y, half + z);
                    if (!keepGoing) {
                        return;
                    }
                }
            }
        }
    }

    public static int manhanttanDistance(Point from, Point to) {
        int dx = Math.abs(from.blockX() - to.blockX());
        int dy = Math.abs(from.blockY() - to.blockY());
        int dz = Math.abs(from.blockZ() - to.blockZ());
        return dx + dy + dz;
    }

    private interface OffsetConsumer {
        boolean accept(int x, int y, int z);
    }
}
