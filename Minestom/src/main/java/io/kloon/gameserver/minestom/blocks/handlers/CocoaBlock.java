package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.BlockProp;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.IntProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;

public class CocoaBlock {
    public static final IntProp AGE = new IntProp("age", 3);
    public static final EnumProp<FacingXZ> FACING = BlockProp.FACING_XZ;
}
