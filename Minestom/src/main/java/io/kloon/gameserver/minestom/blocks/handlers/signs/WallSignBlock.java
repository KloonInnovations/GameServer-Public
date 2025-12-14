package io.kloon.gameserver.minestom.blocks.handlers.signs;

import io.kloon.gameserver.minestom.blocks.handlers.WaterBlock;
import io.kloon.gameserver.minestom.blocks.properties.BlockProp;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;

public class WallSignBlock {
    public static final EnumProp<FacingXZ> FACING = BlockProp.FACING_XZ;
    public static final BooleanProp WATERLOGGED = WaterBlock.WATERLOGGED;
}
