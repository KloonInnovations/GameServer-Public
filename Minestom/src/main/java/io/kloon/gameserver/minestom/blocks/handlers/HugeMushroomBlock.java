package io.kloon.gameserver.minestom.blocks.handlers;

import com.google.common.collect.Sets;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;

import java.util.Set;

public class HugeMushroomBlock {
    public static final BooleanProp DOWN = new BooleanProp("down");
    public static final BooleanProp UP = new BooleanProp("up");
    public static final BooleanProp EAST = new BooleanProp("east");
    public static final BooleanProp WEST = new BooleanProp("west");
    public static final BooleanProp NORTH = new BooleanProp("north");
    public static final BooleanProp SOUTH = new BooleanProp("south");

    public static final Set<Block> BLOCKS = Sets.newHashSet(Block.BROWN_MUSHROOM_BLOCK, Block.RED_MUSHROOM_BLOCK, Block.MUSHROOM_STEM);

    public static BooleanProp getFromDirection(Direction direction) {
        return switch (direction) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }
}
