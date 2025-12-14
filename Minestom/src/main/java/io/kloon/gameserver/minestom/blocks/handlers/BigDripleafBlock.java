package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.BigDripleafTilt;

public class BigDripleafBlock extends FacingXZBlock {
    public static final EnumProp<BigDripleafTilt> TILT = new EnumProp<>("tilt", BigDripleafTilt.class);
    public static final BooleanProp WATERLOGGED = WaterBlock.WATERLOGGED;
}
