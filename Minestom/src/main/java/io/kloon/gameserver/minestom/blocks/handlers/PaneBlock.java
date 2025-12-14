package io.kloon.gameserver.minestom.blocks.handlers;

import com.google.common.collect.Sets;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;

import java.util.Set;

public class PaneBlock {
    public static final BooleanProp EAST = new BooleanProp("east");
    public static final BooleanProp NORTH = new BooleanProp("north");
    public static final BooleanProp SOUTH = new BooleanProp("south");
    public static final BooleanProp WEST = new BooleanProp("west");
    public static final BooleanProp WATERLOGGED = WaterBlock.WATERLOGGED;

    public static BooleanProp fromDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
            default -> throw new IllegalStateException("Unsupported direction " + direction);
        };
    }

    public static final Set<Block> BLOCKS = Sets.newHashSet(
            Block.IRON_BARS,
            Block.GLASS_PANE,
            Block.WHITE_STAINED_GLASS_PANE,
            Block.GRAY_STAINED_GLASS_PANE,
            Block.LIGHT_GRAY_STAINED_GLASS_PANE,
            Block.BLACK_STAINED_GLASS_PANE,
            Block.BROWN_STAINED_GLASS_PANE,
            Block.RED_STAINED_GLASS_PANE,
            Block.ORANGE_STAINED_GLASS_PANE,
            Block.YELLOW_STAINED_GLASS_PANE,
            Block.LIME_STAINED_GLASS_PANE,
            Block.GREEN_STAINED_GLASS_PANE,
            Block.CYAN_STAINED_GLASS_PANE,
            Block.LIGHT_BLUE_STAINED_GLASS_PANE,
            Block.BLUE_STAINED_GLASS_PANE,
            Block.PURPLE_STAINED_GLASS_PANE,
            Block.MAGENTA_STAINED_GLASS_PANE,
            Block.PINK_STAINED_GLASS_PANE
    );
}
