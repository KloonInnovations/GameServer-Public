package io.kloon.gameserver.minestom.blocks.properties.enums;

import io.kloon.gameserver.minestom.blocks.transforms.RotationTransform;
import net.minestom.server.instance.block.BlockFace;

public enum FacingHopper {
    DOWN,
    EAST,
    NORTH,
    SOUTH,
    WEST,
    ;

    public FacingXZ toFacing() {
        return switch (this) {
            case DOWN -> throw new IllegalStateException();
            case EAST -> FacingXZ.EAST;
            case NORTH -> FacingXZ.NORTH;
            case SOUTH -> FacingXZ.SOUTH;
            case WEST -> FacingXZ.WEST;
        };
    }

    public FacingHopper opposite() {
        return switch (this) {
            case DOWN -> DOWN;
            case EAST -> WEST;
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case WEST -> EAST;
        };
    }

    public FacingHopper rotateClockwise() {
        return switch (this) {
            case DOWN -> DOWN;
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };
    }

    public FacingHopper rotateCounterClockwise() {
        return switch (this) {
            case DOWN -> DOWN;
            case NORTH -> WEST;
            case WEST -> SOUTH;
            case SOUTH -> EAST;
            case EAST -> NORTH;
        };
    }

    public FacingHopper rotate(RotationTransform rotation) {
        return switch (rotation) {
            case NONE -> this;
            case CLOCKWISE_90 -> rotateClockwise();
            case CLOCKWISE_180 -> opposite();
            case CLOCKWISE_270 -> rotateCounterClockwise();
        };
    }

    public static FacingHopper fromBlockFace(BlockFace face) {
        return switch (face) {
            case BOTTOM, TOP -> DOWN;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }
}
