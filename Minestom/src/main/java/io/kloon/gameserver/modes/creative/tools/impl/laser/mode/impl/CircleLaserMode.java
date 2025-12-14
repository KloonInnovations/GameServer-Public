package io.kloon.gameserver.modes.creative.tools.impl.laser.mode.impl;

import io.kloon.gameserver.modes.creative.tools.impl.laser.mode.LaserMode;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.util.joml.JomlUtils.threef;

public class CircleLaserMode implements LaserMode {
    private static final Vec UP = new Vec(0, 1, 0);

    @Override
    public List<BlockVec> getBlocksAround(Point center, Vec dir, int radius) {
        List<BlockVec> list = new ArrayList<>();

        Vector3f up = dir.equals(UP) || dir.equals(UP.neg())
                ? new Vector3f(0, 0, 1)
                : new Vector3f(0, 1, 0);

        Matrix4f mat = new Matrix4f()
                .rotationTowards(threef(dir), up);
        int radiusSq = radius * radius;

        for (int x = -radius; x <= radius; ++x) {
            for (int y = -radius; y <= radius; ++y) {
                double distSq = (x*x) + (y*y);
                if (distSq > radiusSq) continue;

                Vector3f rotated = mat.transformPosition(new Vector3f(x + 0.5f, y + 0.5f, 0));
                Vec point = new Vec(rotated.x, rotated.y, rotated.z)
                        .add(center);
                list.add(new BlockVec(point));
            }
        }

        return list;
    }
}
