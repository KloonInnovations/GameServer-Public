package io.kloon.gameserver.modes.creative.tools.impl.copypaste.settings;

import io.kloon.gameserver.minestom.blocks.transforms.RotationTransform;
import io.kloon.infra.util.EnumQuery;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static io.kloon.gameserver.util.joml.JomlUtils.threef;
import static io.kloon.gameserver.util.joml.JomlUtils.unthreef;

public enum ClipRotation {
    ZERO("0", 0),
    NINETY("90", Math.PI / 2),
    ONE_EIGHTY("180", Math.PI),
    TWO_SEVENTY("270", Math.PI * 1.5),
    ;

    private final String dbKey;
    private final float rad;

    ClipRotation(String dbKey, double rad) {
        this.dbKey = dbKey;
        this.rad = (float) rad;
    }

    public String getDbKey() {
        return dbKey;
    }

    public float getRad() {
        return rad;
    }

    public static final EnumQuery<String, ClipRotation> BY_DB_KEY = new EnumQuery<>(ClipRotation.values(), c -> c.dbKey);

    public String label() {
        return switch (this) {
            case ZERO -> "No rotation";
            case NINETY -> "90 degrees";
            case ONE_EIGHTY -> "180 degrees";
            case TWO_SEVENTY -> "270 degrees";
        };
    }

    public ClipRotation clockwise() {
        return switch (this) {
            case ZERO -> NINETY;
            case NINETY -> ONE_EIGHTY;
            case ONE_EIGHTY -> TWO_SEVENTY;
            case TWO_SEVENTY -> ZERO;
        };
    }

    public ClipRotation counterwise() {
        return switch (this) {
            case ZERO -> TWO_SEVENTY;
            case NINETY -> ZERO;
            case ONE_EIGHTY -> NINETY;
            case TWO_SEVENTY -> ONE_EIGHTY;
        };
    }

    public Vec floorOffset() {
        return switch (this) {
            case ZERO -> Vec.ZERO;
            case NINETY -> new Vec(1, 0, 0);
            case ONE_EIGHTY -> new Vec(1, 0, 1);
            case TWO_SEVENTY -> new Vec(0, 0, 1);
        };
    }

    public RotationTransform toTransform() {
        return switch (this) {
            case ZERO -> RotationTransform.NONE;
            case NINETY -> RotationTransform.CLOCKWISE_90;
            case ONE_EIGHTY -> RotationTransform.CLOCKWISE_180;
            case TWO_SEVENTY -> RotationTransform.CLOCKWISE_270;
        };
    }

    public BoundingBox rotated(Point origin, BoundingBox noRotBb) {
        Point pos1 = noRotBb.relativeStart();
        Point pos2 = noRotBb.relativeEnd();

        Vector3f pos1f = threef(pos1);
        Vector3f originf = threef(origin);

        Matrix4f matrix = new Matrix4f()
                .translate(originf)
                .rotateY(-rad)
                .translate(originf.negate(new Vector3f()));

        pos1 = unthreef(matrix.transformPosition(pos1f));
        pos2 = unthreef(matrix.transformPosition(threef(pos2)));

        return BoundingBox.fromPoints(pos1, pos2);
    }

    public Cuboid rotated(Point origin, Cuboid cuboid) {
        Vector3f af = threef(cuboid.a());
        Vector3f bf = threef(cuboid.b());

        Matrix4f matrix = rotatedMatrixAroundOrigin(origin, false);

        Point a = unthreef(matrix.transformPosition(af));
        Point b = unthreef(matrix.transformPosition(bf));

        return new Cuboid(a, b);
    }

    public Vec rotated(Point origin, Point point) {
        Matrix4f matrix = rotatedMatrixAroundOrigin(origin, false);
        return unthreef(matrix.transformPosition(threef(point)));
    }

    public Matrix4f rotatedMatrixAroundOrigin(Point origin, boolean reverse) {
        Vector3f origin3f = threef(origin);

        float angle = reverse ? rad : -rad; // right-handed coordinates

        return new Matrix4f()
                .translate(origin3f)
                .rotateY(angle)
                .translate(origin3f.negate(new Vector3f()));
    }
}
