package io.kloon.gameserver.creative.menu.create.dimensions;

import net.minestom.server.item.Material;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.DimensionType;

public record DimensionChoice(
        RegistryKey<DimensionType> dimensionType,
        String name,
        Material icon
) {
    static {

    }
}
