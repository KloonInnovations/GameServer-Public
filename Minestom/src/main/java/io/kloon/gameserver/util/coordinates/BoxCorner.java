package io.kloon.gameserver.util.coordinates;

import io.kloon.infra.util.EnumQuery;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;

public enum BoxCorner {
    BOTTOM_ORIGIN(0, 0, 0),
    BOTTOM_X(1, 0, 0),
    BOTTOM_Z(0, 0, 1),
    BOTTOM_FAR(1, 0, 1),

    TOP_ORIGIN(0, 1, 0),
    TOP_X(1, 1, 0),
    TOP_Z(0, 1, 1),
    TOP_FAR(1, 1, 1),
    ;

    private final Vec vec;

    BoxCorner(double x, double y, double z) {
        this.vec = new Vec(x, y, z);
    }

    public double x() {
        return vec.x();
    }

    public double y() {
        return vec.y();
    }

    public double z() {
        return vec.z();
    }

    public Vec vec() {
        return vec;
    }

    public BoxCorner opposite() {
        Vec oppositeSignum = new Vec(
                vec.x() == 0 ? 1 : 0,
                vec.y() == 0 ? 1 : 0,
                vec.z() == 0 ? 1 : 0);
        return BY_SIGNUM.get(oppositeSignum);
    }

    public Vec mul(double mult) {
        return vec.mul(mult);
    }

    public Vec onBox(BoundingBox box) {
        return vec().mul(box.width(), box.height(), box.depth()).add(box.relativeStart());
    }

    // finds the closest corner
    public static BoxCorner fromBox(BoundingBox box, Point point) {
        Point center = box.relativeEnd().sub(box.relativeEnd()).mul(0.5);
        Vec outwards = Vec.fromPoint(point.sub(center));
        Vec signum = outwards.apply(Vec.Operator.SIGNUM);
        return BY_SIGNUM.get(signum);
    }

    public static final BoxCorner[] VALUES = values();
    public static final EnumQuery<Vec, BoxCorner> BY_SIGNUM = new EnumQuery<>(BoxCorner.values(), BoxCorner::vec);
}
