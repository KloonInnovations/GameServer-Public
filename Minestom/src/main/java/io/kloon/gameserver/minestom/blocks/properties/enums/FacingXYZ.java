package io.kloon.gameserver.minestom.blocks.properties.enums;

import com.google.common.collect.ImmutableBiMap;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.utils.Direction;

public enum FacingXYZ {
    NORTH,
    EAST,
    SOUTH,
    WEST,
    UP,
    DOWN,
    ;

    private static final ImmutableBiMap<FacingXYZ, Direction> DIRECTION = ImmutableBiMap.<FacingXYZ, Direction>builder()
            .put(FacingXYZ.NORTH, Direction.NORTH)
            .put(FacingXYZ.EAST, Direction.EAST)
            .put(FacingXYZ.SOUTH, Direction.SOUTH)
            .put(FacingXYZ.WEST, Direction.WEST)
            .put(FacingXYZ.UP, Direction.UP)
            .put(FacingXYZ.DOWN, Direction.DOWN)
            .build();

    public Direction toDirection() {
        return DIRECTION.get(this);
    }

    public static FacingXYZ fromDirection(Direction direction) {
        return DIRECTION.inverse().get(direction);
    }

    public static FacingXYZ fromBlockFace(BlockFace face) {
        return fromDirection(face.toDirection());
    }
}
