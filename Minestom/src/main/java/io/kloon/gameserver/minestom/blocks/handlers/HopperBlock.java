package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingHopper;

public class HopperBlock {
    public static final BooleanProp ENABLED = new BooleanProp("enabled");
    public static final EnumProp<FacingHopper> FACING = new EnumProp<>("facing", FacingHopper.class);
}
