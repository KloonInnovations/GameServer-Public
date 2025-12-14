package io.kloon.gameserver.modes.creative.tools.impl.layer.work;

import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.tools.impl.layer.params.LayerShape;
import io.kloon.gameserver.util.coordinates.Axis;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;

public record LayerGen(
        Point center,
        MaskLookup mask,
        CreativePattern pattern,
        double radius,
        Axis axis,
        LayerShape shape
) {
    public BoundingBox computeBoundingBox() {
        return computeBoundingBox(center, radius, axis);
    }

    public static BoundingBox computeBoundingBox(Point center, double radius, Axis axis) {
        double length = radius * 2 + 1;

        Vec dimensions = new Vec(
                axis == Axis.X ? 1 : length,
                axis == Axis.Y ? 1 : length,
                axis == Axis.Z ? 1 : length
        );

        Vec start = new Vec(
                axis == Axis.X ? center.blockX() : Math.floor(center.x() - radius),
                axis == Axis.Y ? center.blockY() : Math.floor(center.y() - radius),
                axis == Axis.Z ? center.blockZ() : Math.floor(center.z() - radius)
        );

        return new BoundingBox(dimensions.x(), dimensions.y(), dimensions.z(), start);
    }

    public double radiusSq() {
        return radius * radius;
    }
}
