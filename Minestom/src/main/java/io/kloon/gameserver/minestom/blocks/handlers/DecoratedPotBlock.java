package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;

public class DecoratedPotBlock extends FacingXZBlock {
    public static final BooleanProp CRACKED = new BooleanProp("cracked");
    public static final BooleanProp WATERLOGGED = WaterBlock.WATERLOGGED;
}
