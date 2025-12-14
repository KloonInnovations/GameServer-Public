package io.kloon.gameserver.modes.creative.tools.impl.erosion.work;

import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.params.ErosionParams;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.BlockVec;

public record ErosionGen(
        BlockVec center,
        MaskLookup mask,
        int radius,
        boolean sphere,
        ErosionParams params
) {
    public static final int DEFAULT_RADIUS = 5;
    public static final int MAX_RADIUS = 21;

    public BoundingBox boundingBox() {
        return BoundingBoxUtils.aroundCenterOdd(center, radius);
    }

    public double radiusSquared() {
        return radius * radius;
    }
}
