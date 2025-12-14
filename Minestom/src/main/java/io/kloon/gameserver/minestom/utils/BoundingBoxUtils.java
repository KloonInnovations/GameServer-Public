package io.kloon.gameserver.minestom.utils;

import io.kloon.gameserver.util.coordinates.Axis;
import io.kloon.gameserver.util.physics.BoundingBoxFace;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class BoundingBoxUtils {
    private static final Logger LOG = LoggerFactory.getLogger(BoundingBoxUtils.class);

    private BoundingBoxUtils() {
    }

    public static BoundingBox fromPoints(Point a, Point b) {
        Vec aVec = Vec.fromPoint(a);
        Vec min = aVec.min(b);
        Vec max = aVec.max(b);
        Vec dimensions = max.sub(min);
        return new BoundingBox(dimensions.x(), dimensions.y(), dimensions.z(), min);
    }

    public static BoundingBox fromBlock(Point blockPos) {
        return new BoundingBox(1, 1, 1, new BlockVec(blockPos));
    }

    public static double blocksVolume(BoundingBox bb) {
        return bb.width() * bb.height() * bb.depth();
    }

    public static int blocksVolume(BlockVec dimensions) {
        return dimensions.blockX()
                * dimensions.blockY()
                * dimensions.blockZ();
    }

    public static long volumeRounded(BoundingBox bb) {
        return Math.round(blocksVolume(bb));
    }

    public static Vec getCenter(BoundingBox bb) {
        Point start = bb.relativeStart();
        Point end = bb.relativeEnd();

        return new Vec(
                (end.x() + start.x()) / 2,
                (end.y() + start.y()) / 2,
                (end.z() + start.z()) / 2
        );
    }

    public static BoundingBox aroundCenterOdd(Point center, int radius) {
        Point centerOfCenter = center.add(0.5, 0.5, 0.5);
        Point start = centerOfCenter.sub(radius + 0.5);
        Point end = centerOfCenter.add(radius + 0.5);
        return BoundingBox.fromPoints(start, end);
    }

    public static double dimension(BoundingBox bb, Axis axis) {
        return switch (axis) {
            case Axis.X -> bb.width();
            case Axis.Y -> bb.height();
            case Axis.Z -> bb.depth();
        };
    }

    public static BoundingBoxFace computeFaceFromNormal(BoundingBox bb, Vec normal) {
        List<Vec> points = getPoints(bb);
        Axis normalAxis = Axis.byVec(normal);

        Point onePointOnFace = points.stream().max(Comparator.comparingDouble(point -> point.normalize().dot(normal))).get();
        double onePointAxisCoord = normalAxis.get(onePointOnFace);

        List<Vec> pointsOnFace = points.stream().filter(vec -> {
            double axisCoord = normalAxis.get(vec);
            return axisCoord == onePointAxisCoord;
        }).toList();

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        double maxZ = Double.MIN_VALUE;
        for (Vec pointOnFace : pointsOnFace) {
            minX = Math.min(minX, pointOnFace.x());
            minY = Math.min(minY, pointOnFace.y());
            minZ = Math.min(minZ, pointOnFace.z());
            maxX = Math.max(maxX, pointOnFace.x());
            maxY = Math.max(maxY, pointOnFace.y());
            maxZ = Math.max(maxZ, pointOnFace.z());
        }

        Vec min = new Vec(minX, minY, minZ);
        Vec max = new Vec(maxX, maxY, maxZ);

        return new BoundingBoxFace(min, max);
    }

    public static List<Vec> getPoints(BoundingBox bb) {
        Point a = bb.relativeStart();
        Point b = bb.relativeEnd();

        List<Vec> points = new ArrayList<>();
        points.add(new Vec(a.x(), a.y(), a.z()));
        points.add(new Vec(a.x(), a.y(), b.z()));
        points.add(new Vec(a.x(), b.y(), a.z()));
        points.add(new Vec(a.x(), b.y(), b.z()));
        points.add(new Vec(b.x(), a.y(), a.z()));
        points.add(new Vec(b.x(), a.y(), b.z()));
        points.add(new Vec(b.x(), b.y(), a.z()));
        points.add(new Vec(b.x(), b.y(), b.z()));
        return points;
    }

    public static String fmt(BoundingBox bb) {
        Point start = bb.relativeStart();
        Point end = bb.relativeEnd();
        return STR."(\{start.blockX()},\{start.blockY()},\{start.blockZ()}) to (\{end.blockX()},\{end.blockY()},\{end.blockZ()})";
    }

    public static Vec dimensions(BoundingBox bb) {
        return new Vec(bb.width(), bb.height(), bb.depth());
    }

    public static String fmtDimensions(BoundingBox bb) {
        return STR."\{(int) bb.width()}x\{(int) bb.height()}x\{(int) bb.depth()}";
    }

    public static String fmtDimensions(Vec dimensions) {
        return STR."\{(int) dimensions.x()}x\{(int) dimensions.y()}x\{(int) dimensions.z()}";
    }
}
