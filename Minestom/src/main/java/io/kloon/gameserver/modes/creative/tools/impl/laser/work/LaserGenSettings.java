package io.kloon.gameserver.modes.creative.tools.impl.laser.work;

import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.tools.impl.laser.mode.LaserModeType;
import net.minestom.server.coordinate.Vec;

public record LaserGenSettings(
        CreativePattern pattern,
        MaskLookup mask,
        Vec eyePos,
        Vec end,
        LaserModeType mode,
        int radius,
        double offset,
        boolean ignoreBlocks
) {
    public static final int DEFAULT_RADIUS = 1;
    public static final int MAX_RADIUS = 7;

    public static final int DEFAULT_OFFSET = 2;
    public static final int MAX_OFFSET = 9;
}
