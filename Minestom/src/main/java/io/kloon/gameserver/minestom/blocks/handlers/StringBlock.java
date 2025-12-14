package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import net.minestom.server.utils.Direction;

public class StringBlock {
    public static final BooleanProp EAST = new BooleanProp("east");
    public static final BooleanProp NORTH = new BooleanProp("north");
    public static final BooleanProp SOUTH = new BooleanProp("south");
    public static final BooleanProp WEST = new BooleanProp("west");

    public static BooleanProp fromDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
            default -> throw new IllegalStateException("Unsupported direction " + direction);
        };
    }
}
