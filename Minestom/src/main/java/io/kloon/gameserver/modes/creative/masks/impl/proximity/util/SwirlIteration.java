package io.kloon.gameserver.modes.creative.masks.impl.proximity.util;

import io.kloon.gameserver.minestom.utils.DirectionUtils;
import io.kloon.gameserver.util.coordinates.Axis;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.utils.Direction;

import java.util.function.Function;

import static net.minestom.server.utils.Direction.*;

public class SwirlIteration {
    public static void iterate(Point center, int radius, Axis axis, Function<Point, Boolean> consumer) {
        Direction direction = axis == Axis.X ? Direction.NORTH : Direction.WEST;
        Vec offset = Vec.ZERO;
        int max = radius * 2;
        if (!consumer.apply(center)) {
            return;
        }
        for (int i = 1; i <= max; ++i) {
            for (int j = 0; j < i; ++j) {
                offset = offset.add(direction.vec());
                if (!consumer.apply(center.add(offset))) {
                    return;
                }
            }
            direction = clockwise(direction, axis);

            for (int j = 0; j < i; ++j) {
                offset = offset.add(direction.vec());
                if (!consumer.apply(center.add(offset))) {
                    return;
                }
            }
            direction = clockwise(direction, axis);

            if (i == max) {
                for (int j = 0; j < i; ++j) {
                    offset = offset.add(direction.vec());
                    if (!consumer.apply(center.add(offset))) {
                        return;
                    }
                }
            }
        }
    }

    private static Direction clockwise(Direction direction, Axis axis) {
        return switch (axis) {
            case X -> switch (direction) {
                case SOUTH -> DOWN;
                case DOWN -> NORTH;
                case NORTH -> UP;
                case UP -> SOUTH;
                default -> throw new IllegalStateException("Unsupported rotation");
            };
            case Y -> DirectionUtils.clockwise(direction);
            case Z -> switch (direction) {
                case EAST -> DOWN;
                case DOWN -> WEST;
                case WEST -> UP;
                case UP -> EAST;
                default -> throw new IllegalStateException("Unsupported rotation");
            };
        };
    }
}
