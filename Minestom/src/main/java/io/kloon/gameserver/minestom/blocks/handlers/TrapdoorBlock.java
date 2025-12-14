package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.BlockProp;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;

public class TrapdoorBlock {
    public static final EnumProp<FacingXZ> FACING = BlockProp.FACING_XZ;
    public static final EnumProp<Half> HALF = new EnumProp<>("half", Half.class);
    public static final BooleanProp OPEN = new BooleanProp("open");
    public static final BooleanProp POWERED = new BooleanProp("powered");
    public static final BooleanProp WATERLOGGED = WaterBlock.WATERLOGGED;

    public enum Half {
        BOTTOM,
        TOP
    }
}
