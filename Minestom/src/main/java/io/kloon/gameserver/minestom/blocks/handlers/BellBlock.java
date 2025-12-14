package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.BlockProp;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;

public class BellBlock {
    public static final EnumProp<FacingXZ> FACING = BlockProp.FACING_XZ;
    public static final EnumProp<Attachment> ATTACHMENT = new EnumProp<>("attachment", Attachment.class);
    public static final BooleanProp POWERED = new BooleanProp("powered");

    public enum Attachment {
        CEILING,
        DOUBLE_WALL,
        FLOOR,
        SINGLE_WALL
    }
}
