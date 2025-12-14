package io.kloon.gameserver.modes.creative.storage.blockvolume.util;

import net.minestom.server.coordinate.Point;

public record IntPos(int x, int y, int z) {
    public IntPos(Point point) {
        this(point.blockX(), point.blockY(), point.blockZ());
    }
}
