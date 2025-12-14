package io.kloon.gameserver.util.physics;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

public record Ray(Vec origin, Vec dir, Vec invDir) {
    public Ray(Point origin, Vec dir) {
        this(Vec.fromPoint(origin), dir, invert(dir));
    }

    public static Ray fromPos(Pos pos) {
        return new Ray(pos, pos.direction());
    }

    private static Vec invert(Vec vec) {
        return new Vec(
                1.0 / vec.x(),
                1.0 / vec.y(),
                1.0 / vec.z());
    }
}
