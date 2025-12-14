package io.kloon.gameserver.util.coordinates;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.BlockFace;

import java.util.Arrays;
import java.util.Comparator;

public enum CardinalDirection {
    WEST(-1, 0, 0, Axis.X),
    EAST(1, 0, 0, Axis.X),
    DOWN(0, -1, 0, Axis.Y),
    UP(0, 1, 0, Axis.Y),
    NORTH(0, 0, -1, Axis.Z),
    SOUTH(0, 0, 1, Axis.Z),
    ;

    private final Vec vec;
    private final BlockVec blockVec;
    private final Axis axis;

    CardinalDirection(double x, double y, double z, Axis axis) {
        this.vec = new Vec(x, y, z);
        this.blockVec = new BlockVec(x, y, z);
        this.axis = axis;
    }

    public double x() {
        return vec.x();
    }

    public double y() {
        return vec.y();
    }

    public double z() {
        return vec.z();
    }

    public Vec vec() {
        return vec;
    }

    public BlockVec blockVec() {
        return blockVec;
    }

    public Vec mul(double mult) {
        return vec.mul(mult);
    }

    public Vec zerosAsOne() {
        return new Vec(
                vec.x() == 0 ? 1 : 0,
                vec.y() == 0 ? 1 : 0,
                vec.z() == 0 ? 1 : 0
        );
    }

    public CardinalDirection reverse() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
            case UP -> DOWN;
            case DOWN -> UP;
        };
    }

    public Axis axis() {
        return axis;
    }

    public boolean positive() {
        return vec.x() > 0 || vec.y() > 0 || vec.z() > 0;
    }

    public boolean negative() {
        return !positive();
    }

    public boolean vertical() {
        return this == UP || this == DOWN;
    }

    public boolean horizontal() {
        return !vertical();
    }

    public static CardinalDirection closestDir(Vec dir) {
        Vec norm = dir.normalize();
        return Arrays.stream(values())
                .max(Comparator.comparingDouble(card -> card.vec().dot(norm)))
                .orElseThrow(() -> new IllegalStateException("Invalid looking vec"));
    }

    public static CardinalDirection fromVec(Vec vec) {
        if (vec.x() != 0) {
            return vec.x() < 0 ? WEST : EAST;
        } else if (vec.y() != 0) {
            return vec.y() < 0 ? DOWN : UP;
        }
        return vec.z() < 0 ? NORTH : SOUTH;
    }

    public static CardinalDirection fromFace(BlockFace face) {
        return switch (face) {
            case BOTTOM -> DOWN;
            case TOP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    public static BlockFace[] FACES = BlockFace.values();
}
