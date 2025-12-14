package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.family.BlockFamily;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;

import java.util.Set;

public class FenceBlock {
    public static final BooleanProp EAST = new BooleanProp("east");
    public static final BooleanProp NORTH = new BooleanProp("north");
    public static final BooleanProp SOUTH = new BooleanProp("south");
    public static final BooleanProp WEST = new BooleanProp("west");
    public static final BooleanProp WATERLOGGED = WaterBlock.WATERLOGGED;

    public static final Set<Block> BLOCKS = BlockFamily.getBlocksOfVariant(BlockFamily.Variant.FENCE);

    public static BooleanProp fromDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
            default -> throw new IllegalStateException("Invalid direction " + direction);
        };
    }

    private static boolean cannotAttach(Block block) {
        block = block.defaultState();
        if (!block.isSolid()) {
            return true;
        }

        return block.name().contains("leaves")
               || block == Block.BARRIER || block == Block.CARVED_PUMPKIN || block == Block.JACK_O_LANTERN || block == Block.LECTERN
               || WallBlock.BLOCKS.contains(block) || FenceBlock.BLOCKS.contains(block) || PaneBlock.BLOCKS.contains(block)
               || block == Block.MELON || block == Block.PUMPKIN || block.name().contains("shulker_block");
    }

    public static boolean canAttach(Block block) {
        return !cannotAttach(block.defaultState());
    }
}
