package io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.work;

import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;

public record PyramidGenSettings(
        BlockVec bottomCenter,
        CreativePattern pattern,
        MaskLookup mask,
        int steps,
        int stepHeight,
        int stepLength,
        boolean hollow,
        boolean upsideDown
) {
    public static final int DEFAULT_STEP = 6;
    public static final int DEFAULT_STEP_HEIGHT = 1;
    public static final int DEFAULT_STEP_LENGTH = 1;

    public static final int MAX_STEPS = 90;
    public static final int MAX_STEP_HEIGHT = 16;
    public static final int MAX_STEP_LENGTH = 16;

    public BoundingBox computeBoundingBox() {
        double side = (steps * 2 - 1) * stepLength;
        Vec offset = new Vec(side, 0, side).mul(0.5).apply(Vec.Operator.FLOOR);
        Point bbStart = bottomCenter.sub(offset);

        double height = steps * stepHeight;
        return new BoundingBox(side, height, side, bbStart);
    }
}
