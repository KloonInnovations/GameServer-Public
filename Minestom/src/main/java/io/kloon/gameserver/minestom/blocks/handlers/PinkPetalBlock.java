package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.BlockProp;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.IntProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;

public class PinkPetalBlock {
    public static final EnumProp<FacingXZ> FACING = BlockProp.FACING_XZ;
    public static final IntProp FLOWER_AMOUNT = new IntProp("flower_amount", 1, 5);
}
