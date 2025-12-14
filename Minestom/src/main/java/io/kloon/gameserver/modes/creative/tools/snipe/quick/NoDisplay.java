package io.kloon.gameserver.modes.creative.tools.snipe.quick;

import net.minestom.server.color.Color;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

public class NoDisplay implements QuickDisplay {
    @Override
    public QuickDisplay withMaterial(Material material) {
        return this;
    }

    @Override
    public QuickDisplay withGlowColor(@Nullable Color color) {
        return this;
    }

    @Override
    public void update(BlockVec target, double radius, boolean odd, boolean animate) {

    }

    @Override
    public void remove() {

    }
}
