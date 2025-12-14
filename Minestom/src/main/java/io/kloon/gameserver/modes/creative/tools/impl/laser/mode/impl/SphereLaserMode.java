package io.kloon.gameserver.modes.creative.tools.impl.laser.mode.impl;

import io.kloon.gameserver.modes.creative.tools.impl.laser.mode.LaserMode;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;

import java.util.ArrayList;
import java.util.List;

public class SphereLaserMode implements LaserMode {
    @Override
    public List<BlockVec> getBlocksAround(Point center, Vec dir, int radius) {
        List<BlockVec> list = new ArrayList<>();
        int radiusSq = radius * radius;

        for (int x = -radius; x <= radius; ++x) {
            for (int y = -radius; y <= radius; ++y) {
                for (int z = -radius; z <= radius; ++z) {
                    double distSq = x*x + y*y + z*z;
                    if (distSq > radiusSq) continue;

                    list.add(new BlockVec(center.add(x, y, z)));
                }
            }
        }

        return list;
    }
}
