package io.kloon.gameserver.modes.creative.tools.impl.blend.work;

import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.tools.impl.blend.BlendSampling;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.BlockVec;

public record BlendGen(
        BlockVec center,
        MaskLookup mask,
        int radius,
        BlendSampling sampling,
        boolean sphere,
        boolean doNotSampleAir,
        boolean doNotSampleLiquid,
        boolean doNotChangeOnTies
) {
    public BoundingBox boundingBox() {
        return BoundingBoxUtils.aroundCenterOdd(center, radius);
    }

    public int radiusSquared() {
        return radius * radius;
    }
}
