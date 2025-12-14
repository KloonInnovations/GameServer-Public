package io.kloon.gameserver.minestom.blocks.handlers.signs;

import io.kloon.gameserver.minestom.blocks.handlers.WaterBlock;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.blocks.properties.IntProp;

public class StandingSignBlock {
    public static final IntProp ROTATION = new IntProp("rotation", 16);
    public static final BooleanProp WATERLOGGED = WaterBlock.WATERLOGGED;
}
