package io.kloon.gameserver.modes.creative.tools.impl.laser.mode;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;

import java.util.List;

public interface LaserMode {
    List<BlockVec> getBlocksAround(Point center, Vec dir, int radius);
}
