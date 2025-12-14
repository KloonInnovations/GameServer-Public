package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.blocks.properties.IntProp;

public class SeaPickleBlock {
    public static final BooleanProp WATERLOGGED = WaterBlock.WATERLOGGED;
    public static final IntProp PICKLES = new IntProp("pickles", 1, 5);
}
