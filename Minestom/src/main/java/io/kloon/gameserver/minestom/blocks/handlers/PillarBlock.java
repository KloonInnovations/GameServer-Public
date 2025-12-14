package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.util.coordinates.Axis;

public class PillarBlock {
    public static final EnumProp<Axis> AXIS = new EnumProp<>("axis", Axis.class);
}
