package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.enums.AttachFace;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;

public class GrindstoneBlock {
    public static final EnumProp<AttachFace> ATTACH_FACE = new EnumProp<>("face", AttachFace.class);
    public static final EnumProp<FacingXZ> FACING = new EnumProp<>("facing", FacingXZ.class);


}
