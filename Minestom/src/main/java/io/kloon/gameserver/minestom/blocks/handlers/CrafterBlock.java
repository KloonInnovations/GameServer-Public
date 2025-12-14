package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FrontAndTop;

public class CrafterBlock {
    public static final EnumProp<FrontAndTop> ORIENTATION = new EnumProp<>("orientation", FrontAndTop.class);
    public static final BooleanProp CRAFTING = new BooleanProp("crafting");
    public static final BooleanProp TRIGGERED = new BooleanProp("triggered");
}
