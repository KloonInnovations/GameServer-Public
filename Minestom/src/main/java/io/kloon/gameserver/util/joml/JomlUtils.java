package io.kloon.gameserver.util.joml;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import org.joml.Vector3f;

public final class JomlUtils {
    public static Vector3f threef(Point point) {
        return new Vector3f(
                (float) point.x(),
                (float) point.y(),
                (float) point.z()
        );
    }

    public static Vec unthreef(Vector3f vec) {
        return new Vec(vec.x, vec.y, vec.z);
    }
}
