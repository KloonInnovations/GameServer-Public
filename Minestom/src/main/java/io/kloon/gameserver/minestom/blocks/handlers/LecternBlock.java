package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;

public class LecternBlock {
    public static final EnumProp<FacingXZ> FACING = FacingXZBlock.FACING_XZ;
    public static final BooleanProp HAS_BOOK = new BooleanProp("has_book");
    public static final BooleanProp POWERED = new BooleanProp("powered");
}
