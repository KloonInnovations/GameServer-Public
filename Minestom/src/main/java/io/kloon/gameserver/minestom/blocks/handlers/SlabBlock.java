package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.SlabType;

public class SlabBlock {
    public static final EnumProp<SlabType> TYPE = new EnumProp<>("type", SlabType.class);
    public static final BooleanProp WATERLOGGED = WaterBlock.WATERLOGGED;
}
