package io.kloon.gameserver.modes.creative.tools.impl.copypaste.settings;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;

public record Cuboid(Point a, Point b) {
    public BoundingBox toBoundingBox() {
        return BoundingBox.fromPoints(a, b);
    }
}
