package io.kloon.gameserver.modes.creative.tools.impl.laser.mode.impl;

import io.kloon.gameserver.modes.creative.tools.impl.laser.mode.LaserMode;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;

import java.util.ArrayList;
import java.util.List;

public class CubeLaserMode implements LaserMode {
    @Override
    public List<BlockVec> getBlocksAround(Point center, Vec dir, int radius) {
        List<BlockVec> list = new ArrayList<>();

        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dy = -radius; dy <= radius; ++dy) {
                for (int dz = -radius; dz <= radius; ++dz) {
                    list.add(new BlockVec(center.add(dx, dy, dz)));
                }
            }
        }

        return list;
    }
}
