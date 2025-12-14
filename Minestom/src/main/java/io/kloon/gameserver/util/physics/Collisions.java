package io.kloon.gameserver.util.physics;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

public final class Collisions {
    private Collisions() {}

    // TODO: more efficient
    @Nullable
    public static <T> T raycastThings(Ray ray, Collection<T> things, Function<T, BoundingBox> toBoundingBox) {
        return things.stream().map(thing -> {
                    BoundingBox boundingBox = toBoundingBox.apply(thing);
                    Vec collisionPoint = raycastBoxGetPoint(ray, boundingBox);
                    return collisionPoint == null ? null : new ThingAndPoint<>(thing, collisionPoint);
                }).filter(Objects::nonNull)
                .min(Comparator.comparingDouble(tap -> tap.point.distanceSquared(ray.origin())))
                .map(tap -> tap.thing).orElse(null);
    }

    record ThingAndPoint<T>(T thing, Point point) {}

    // https://tavianator.com/2022/ray_box_boundary.html
    public static boolean raycastBox(Ray ray, BoundingBox boundingBox) {
        double tmin = 0;
        double tmax = Double.POSITIVE_INFINITY;

        double[] rayOrigin = array(ray.origin());
        double[] invDir = array(ray.invDir());

        double[] boxMin = array(boundingBox.relativeStart());
        double[] boxMax = array(boundingBox.relativeEnd());

        for (int d = 0; d < 3; ++d) {
            double t1 = (boxMin[d] - rayOrigin[d]) * invDir[d];
            double t2 = (boxMax[d] - rayOrigin[d]) * invDir[d];

            tmin = Math.max(tmin, Math.min(t1, t2));
            tmax = Math.min(tmax, Math.max(t1, t2));
        }

        return tmin <= tmax;
    }

    @Nullable
    public static Vec raycastBoxGetPoint(Ray ray, BoundingBox boundingBox) {
        return raycastBoxGetPoint(ray, boundingBox, false);
    }

    @Nullable
    public static Vec raycastBoxGetPointFar(Ray ray, BoundingBox boundingBox) {
        return raycastBoxGetPoint(ray, boundingBox, true);
    }

    @Nullable
    private static Vec raycastBoxGetPoint(Ray ray, BoundingBox boundingBox, boolean far) {
        double tmin = 0;
        double tmax = Double.POSITIVE_INFINITY;

        double[] rayOrigin = array(ray.origin());
        double[] invDir = array(ray.invDir());

        double[] boxMin = array(boundingBox.relativeStart());
        double[] boxMax = array(boundingBox.relativeEnd());

        for (int d = 0; d < 3; ++d) {
            double t1 = (boxMin[d] - rayOrigin[d]) * invDir[d];
            double t2 = (boxMax[d] - rayOrigin[d]) * invDir[d];

            tmin = Math.max(tmin, Math.min(t1, t2));
            tmax = Math.min(tmax, Math.max(t1, t2));
        }

        if (tmin > tmax) {
            return null;
        }

        double collisionDist = far
                ? tmin < 0 ? tmin : tmax
                : tmin < 0 ? tmax : tmin;
        Point collisionPoint = ray.origin().add(ray.dir().mul(collisionDist));
        return Vec.fromPoint(collisionPoint);
    }

    @Nullable
    public static Vec raycastBoxGetFaceNormal(Ray ray, BoundingBox boundingBox) {
        return raycastBoxGetFaceNormal(ray, boundingBox, false);
    }

    @Nullable
    public static Vec raycastBoxGetFaceNormalFar(Ray ray, BoundingBox boundingBox) {
        return raycastBoxGetFaceNormal(ray, boundingBox, true);
    }

    @Nullable
    private static Vec raycastBoxGetFaceNormal(Ray ray, BoundingBox boundingBox, boolean far) {
        Vec collision = far
                ? raycastBoxGetPointFar(ray, boundingBox)
                : raycastBoxGetPoint(ray, boundingBox);
        if (collision == null) return null;

        Point center = boundingBox.relativeStart().add(
                boundingBox.width() / 2,
                boundingBox.height() / 2,
                boundingBox.depth() / 2);
        Point centerToCollision = collision.sub(center).div(
                boundingBox.width(),
                boundingBox.height(),
                boundingBox.depth());

        double[] components = array(centerToCollision);
        double maxAbs = Double.NEGATIVE_INFINITY;
        int index = -1;
        int dir = 1;
        for (int d = 0; d < 3; ++d) {
            double component = components[d];
            double abs = Math.abs(component);
            if (abs > maxAbs) {
                maxAbs = abs;
                index = d;
                dir = component >= 0 ? 1 : -1;
            }
        }

        double[] normal = { 0, 0, 0 };
        normal[index] = dir;
        return vec(normal);
    }

    private static double[] array(Point point) {
        return new double[] { point.x(), point.y(), point.z() };
    }

    private static Vec vec(double[] array) {
        return new Vec(array[0], array[1], array[2]);
    }

    public static boolean raycastSphere(Ray ray, Point center, double radius) {
        Vec oc = ray.origin().sub(center);
        double a = ray.dir().dot(ray.dir());
        double b = 2 * oc.dot(ray.dir());
        double c = oc.dot(oc) - radius * radius;
        double discriminant = b * b - 4 * a * c;
        return discriminant > 0;
    }

    // returns the first intersection point, or null
    @Nullable
    public static Point raycastSphereGetPoint(Ray ray, Point center, double radius) {
        Vec oc = ray.origin().sub(center);
        double a = ray.dir().dot(ray.dir());
        double b = 2 * oc.dot(ray.dir());
        double c = oc.dot(oc) - radius * radius;
        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) {
            return null;
        }
        double firstIntersectionDist = (-b - Math.sqrt(discriminant)) / (2 * a);
        return ray.origin().add(ray.dir().mul(firstIntersectionDist));
    }

    public static Point pointOnRay(Ray ray, Point pointInSpace) {
        Vec origin = ray.origin();
        Vec dir = ray.dir();

        Vec toPoint = Vec.fromPoint(pointInSpace.sub(origin));
        double t = toPoint.dot(dir) / dir.dot(dir);
        return origin.add(dir.mul(t));
    }

    public static boolean intersectInclusive(BoundingBox a, BoundingBox b) {
        Point aMin = a.relativeStart();
        Point aMax = a.relativeEnd();

        Point bMin = b.relativeStart();
        Point bMax = b.relativeEnd();

        return aMin.blockX() <= bMax.blockX() && aMax.blockX() >= bMin.blockX()
               && aMin.blockY() <= bMax.blockY() && aMax.blockY() >= bMin.blockY()
               && aMin.blockZ() <= bMax.blockZ() && aMax.blockZ() >= bMin.blockZ();
    }

    public static boolean intersectBlocks(BoundingBox a, BoundingBox b) {
        Point aMin = a.relativeStart();
        Point aMax = a.relativeEnd().sub(1, 1, 1);

        Point bMin = b.relativeStart();
        Point bMax = b.relativeEnd().sub(1, 1, 1);

        return aMin.blockX() <= bMax.blockX() && aMax.blockX() >= bMin.blockX()
                && aMin.blockY() <= bMax.blockY() && aMax.blockY() >= bMin.blockY()
                && aMin.blockZ() <= bMax.blockZ() && aMax.blockZ() >= bMin.blockZ();
    }

    public static boolean contains(BoundingBox box, Point point) {
        Point min = box.relativeStart();
        Point max = box.relativeEnd();
        return point.x() >= min.x() && point.x() <= max.x()
                && point.y() >= min.y() && point.y() <= max.y()
                && point.z() >= min.z() && point.z() <= max.z();
    }

    public static boolean containsExclusive(BoundingBox box, Point point) {
        Point min = box.relativeStart();
        Point max = box.relativeEnd();
        return point.x() >= min.x() && point.x() < max.x()
               && point.y() >= min.y() && point.y() < max.y()
               && point.z() >= min.z() && point.z() < max.z();
    }
}
