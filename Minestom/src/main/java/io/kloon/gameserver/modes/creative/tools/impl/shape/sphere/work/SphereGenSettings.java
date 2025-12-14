package io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.work;

import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;

public record SphereGenSettings(
        BlockVec center,
        double radius,
        CreativePattern pattern,
        boolean hollow,
        boolean odd,
        MaskLookup mask
) {
    public BoundingBox computeBoundingBox() {
        double radius = odd
                ? this.radius + 1.5
                : this.radius + 0.5;
        Vec dimensions = new Vec(radius * 2);
        return new BoundingBox(dimensions.x(), dimensions.y(), dimensions.z(), center.sub(dimensions.div(2)));
    }
}
