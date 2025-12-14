package io.kloon.gameserver.modes.creative.tools.snipe.quick;

import net.minestom.server.color.Color;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

public interface QuickDisplay {
    QuickDisplay withMaterial(Material material);

    QuickDisplay withGlowColor(@Nullable Color color);

    void update(BlockVec target, double radius, boolean odd, boolean animate);

    void remove();
}
