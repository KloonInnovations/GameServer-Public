package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;

import java.util.Arrays;
import java.util.List;

public class VineBlock {
    public static final BooleanProp EAST = new BooleanProp("east");
    public static final BooleanProp WEST = new BooleanProp("west");
    public static final BooleanProp NORTH = new BooleanProp("north");
    public static final BooleanProp SOUTH = new BooleanProp("south");
    public static final BooleanProp UP = new BooleanProp("up");

    public static final List<Direction> DIRECTIONS = Arrays.asList(Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH, Direction.UP);

    public static BooleanProp fromDirection(Direction direction) {
        return switch (direction) {
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
            default -> throw new IllegalStateException("Invalid direction " + direction);
        };
    }

    public static boolean allSidesAreOff(Block block) {
        return DIRECTIONS.stream().noneMatch(dir -> fromDirection(dir).is(block));
    }
}
