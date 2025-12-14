package io.kloon.gameserver.minestom.blocks.properties.enums;

import com.google.common.collect.ImmutableBiMap;
import io.kloon.gameserver.minestom.blocks.transforms.FlipTransform;
import io.kloon.gameserver.minestom.blocks.transforms.RotationTransform;
import io.kloon.gameserver.util.coordinates.Axis;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.utils.Direction;
import net.minestom.server.utils.MathUtils;
import net.minestom.server.utils.position.PositionUtils;

public enum FacingXZ {
    NORTH,
    EAST,
    SOUTH,
    WEST,
    ;

    private static final ImmutableBiMap<FacingXZ, Direction> DIRECTION = ImmutableBiMap.<FacingXZ, Direction>builder()
            .put(FacingXZ.NORTH, Direction.NORTH)
            .put(FacingXZ.EAST, Direction.EAST)
            .put(FacingXZ.SOUTH, Direction.SOUTH)
            .put(FacingXZ.WEST, Direction.WEST)
            .build();

    public Direction toDirection() {
        return DIRECTION.get(this);
    }

    public Axis axis() {
        if (this == EAST || this == WEST) {
            return Axis.X;
        } else {
            return Axis.Z;
        }
    }

    public BlockFace toBlockFace() {
        return switch (this) {
            case NORTH -> BlockFace.NORTH;
            case EAST -> BlockFace.EAST;
            case SOUTH -> BlockFace.SOUTH;
            case WEST -> BlockFace.WEST;
        };
    }

    public FacingXZ opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
        };
    }

    public FacingXZ rotateClockwise() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };
    }

    public FacingXZ rotateCounterClockwise() {
        return switch (this) {
            case NORTH -> WEST;
            case WEST -> SOUTH;
            case SOUTH -> EAST;
            case EAST -> NORTH;
        };
    }

    public double radTo(FacingXZ face) {
        int diff = (face.ordinal() + 4 - ordinal()) % 4;
        return (Math.PI / 2) * diff;
    }

    public RotationTransform transformTo(FacingXZ face) {
        int diff = (face.ordinal() + 4 - ordinal()) % 4;
        return switch (diff) {
            case 1 -> RotationTransform.CLOCKWISE_90;
            case 2 -> RotationTransform.CLOCKWISE_180;
            case 3 -> RotationTransform.CLOCKWISE_270;
            default -> RotationTransform.NONE;
        };
    }

    public FacingXZ rotate(RotationTransform rotation) {
        return switch (rotation) {
            case NONE -> this;
            case CLOCKWISE_90 -> rotateClockwise();
            case CLOCKWISE_180 -> opposite();
            case CLOCKWISE_270 -> rotateCounterClockwise();
        };
    }

    public FacingXZ flip(FlipTransform flip) {
        Axis axis = axis();
        boolean shouldFlip = axis == Axis.Z && flip == FlipTransform.LEFT_RIGHT
                             || axis == Axis.X && flip == FlipTransform.FRONT_BACK;
        return shouldFlip ? opposite() : this;
    }

    public static FacingXZ fromDirection(Direction direction) {
        return DIRECTION.inverse().get(direction);
    }

    public static FacingXZ fromBlockFace(BlockFace face) {
        return fromDirection(face.toDirection());
    }

    public static FacingXZ fromLook(Pos pos) {
        return fromYaw(pos.yaw());
    }

    public static FacingXZ fromPlayer(Player player) {
        return fromLook(player.getPosition());
    }

    public static FacingXZ fromCursorPos(Vec entry) {
        Vec dir = entry.sub(0.5).normalize();
        float yaw = PositionUtils.getLookYaw(dir.x(), dir.z());
        return fromYaw(yaw);
    }

    public static FacingXZ fromYaw(float yaw) {
        Direction dir = MathUtils.getHorizontalDirection(yaw);
        return fromDirection(dir);
    }
}
