package io.kloon.gameserver.util.rendering;

import io.kloon.gameserver.modes.creative.selection.rendering.ExtendoLine;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;

public class LineRender extends ItemDisplayEntity {
    private Point a;
    private Point b;

    private double size = ExtendoLine.PARTS_THICKNESS;

    public LineRender(Point a, Point b) {
        ItemDisplayMeta meta = getEntityMeta();
        meta.setItemStack(ItemStack.of(Material.BLUE_STAINED_GLASS));
        meta.setPosRotInterpolationDuration(1);
        meta.setTransformationInterpolationDuration(1);

        adjust(a, b);
    }

    public Point getA() {
        return a;
    }

    public Point getB() {
        return b;
    }

    public LineRender withMaterial(Material material) {
        getEntityMeta().setItemStack(ItemStack.of(material));
        return this;
    }

    public LineRender withGlowColor(@Nullable Color color) {
        ItemDisplayMeta meta = getEntityMeta();
        if (color == null) {
            meta.setHasGlowingEffect(false);
        } else {
            meta.setHasGlowingEffect(true);
            meta.setGlowColorOverride(color.asRGB());
        }
        return this;
    }

    public LineRender withSize(double size) {
        this.size = size;
        return this;
    }

    public void adjust(Point a, Point b) {
        if (a.equals(this.a) && b.equals(this.b)) {
            return;
        }

        this.a = a;
        this.b = b;

        ItemDisplayMeta meta = getEntityMeta();
        meta.setTransformationInterpolationStartDelta(0);

        double length = a.distance(b);

        meta.setScale(new Vec(size, size, length));

        Vec natural = new Vec(0, 0, 1);
        Vec desired = Vec.fromPoint(b).sub(a).normalize();

        double angle = natural.angle(desired);
        if (angle < Vec.EPSILON || angle > Math.PI - Vec.EPSILON) {
            //meta.setGlowColorOverride(new Color(255, 0, 0).asRGB());
        } else {
            Vec axis = natural.cross(desired).normalize();
            AxisAngle4f axisAngle = new AxisAngle4f((float) angle, (float) axis.x(), (float) axis.y(), (float) axis.z());
            Quaternionf quaternion = new Quaternionf(axisAngle);
            meta.setLeftRotation(new float[]{quaternion.x, quaternion.y, quaternion.z, quaternion.w});
        }

        Pos newPos = new Pos(a.add(desired.mul(length / 2)));
        if (instance == null) {
            position = newPos;
        } else {
            teleport(newPos);
        }
    }

    public static LineRender spawn(Instance instance, Point a, Point b) {
        LineRender render = new LineRender(a, b);
        render.setInstance(instance);
        return render;
    }
}