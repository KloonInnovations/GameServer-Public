package io.kloon.gameserver.util.coordinates;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;

import java.util.Arrays;
import java.util.Comparator;

public enum Axis {
    X(new Vec(1, 0, 0)),
    Y(new Vec(0, 1, 0)),
    Z(new Vec(0, 0, 1));

    private final Vec positiveVec;

    Axis(Vec positiveVec) {
        this.positiveVec = positiveVec;
    }

    public boolean vertical() {
        return this == Y;
    }

    public boolean horizontal() {
        return this == X || this == Z;
    }

    public double get(Point point) {
        return switch (this) {
            case X -> point.x();
            case Y -> point.y();
            case Z -> point.z();
        };
    }

    public static Axis byVec(Vec vec) {
        Vec normalized = vec.normalize();
        return Arrays.stream(values())
                .max(Comparator.comparingDouble(a -> Math.abs(a.positiveVec.dot(normalized))))
                .get();
    }
}
