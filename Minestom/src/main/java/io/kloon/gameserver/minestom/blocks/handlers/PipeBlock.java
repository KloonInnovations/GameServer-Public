package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.utils.Direction;

public class PipeBlock {
    public static final BooleanProp UP = new BooleanProp("up");
    public static final BooleanProp DOWN = new BooleanProp("down");
    public static final BooleanProp NORTH = new BooleanProp("north");
    public static final BooleanProp EAST = new BooleanProp("east");
    public static final BooleanProp SOUTH = new BooleanProp("south");
    public static final BooleanProp WEST = new BooleanProp("west");

    public static BooleanProp getProperty(BlockFace face) {
        return switch (face) {
            case BOTTOM -> DOWN;
            case TOP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    public static BooleanProp getProperty(Direction direction) {
        return getProperty(BlockFace.fromDirection(direction));
    }
}
