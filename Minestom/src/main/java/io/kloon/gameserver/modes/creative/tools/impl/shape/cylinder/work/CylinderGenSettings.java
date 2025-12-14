package io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.work;

import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;

public record CylinderGenSettings(
        BlockVec center,
        CreativePattern pattern,
        MaskLookup mask,
        double radius,
        double thickness,
        boolean hollow,
        boolean odd
) {
    public static final double DEFAULT_RADIUS = 4;
    public static final double DEFAULT_THICKNESS = 6;

    public static final double MAX_RADIUS = 80;
    public static final double MAX_THICKNESS = 80;

    public BoundingBox generateBoundingBox() {
        double radius = odd
                ? this.radius + 1.5
                : this.radius + 0.5;
        Vec dimensions = new Vec(radius * 2, thickness, radius * 2);
        return new BoundingBox(
                dimensions.x(),
                dimensions.y(),
                dimensions.z(),
                center.sub(dimensions.div(2)));
    }
}
