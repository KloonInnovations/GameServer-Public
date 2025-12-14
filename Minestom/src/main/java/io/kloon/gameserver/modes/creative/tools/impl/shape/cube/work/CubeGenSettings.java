package io.kloon.gameserver.modes.creative.tools.impl.shape.cube.work;

import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import net.minestom.server.collision.BoundingBox;

public record CubeGenSettings(
        BoundingBox boundingBox,
        CreativePattern pattern,
        MaskLookup mask,
        boolean hollow
) {
    public static final int MAX_SIZE = 160;

    public boolean isCube() {
        double width = boundingBox.width();
        double height = boundingBox.height();
        double depth = boundingBox.depth();
        return width == height && height == depth;
    }
}
