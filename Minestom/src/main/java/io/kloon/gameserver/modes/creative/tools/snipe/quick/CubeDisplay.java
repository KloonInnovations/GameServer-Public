package io.kloon.gameserver.modes.creative.tools.snipe.quick;

import io.kloon.gameserver.modes.creative.selection.rendering.DisplayCuboid;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

public class CubeDisplay implements QuickDisplay {
    private final Instance instance;

    private DisplayCuboid cubeDisplay;

    private Block material = Block.ORANGE_STAINED_GLASS;
    private Color glowColor = null;

    public CubeDisplay(Instance instance) {
        this.instance = instance;
    }

    @Override
    public void update(BlockVec target, double radius, boolean odd, boolean animate) {
        int oddAdd = odd ? 1 : 0;
        Vec dimensions = new Vec(
                radius * 2 + oddAdd,
                radius * 2 + oddAdd,
                radius * 2 + oddAdd);
        Vec offset = dimensions.mul(0.5).apply(Vec.Operator.FLOOR);
        Vec bbOffset = target.asVec().sub(offset);

        BoundingBox bb = new BoundingBox(dimensions.x(), dimensions.y(), dimensions.z(), bbOffset);
        if (cubeDisplay == null) {
            cubeDisplay = DisplayCuboid.spawn(instance, bb)
                    .withMaterial(material)
                    .withGlowColor(glowColor);
        }
        cubeDisplay.adjust(bb);
    }

    @Override
    public CubeDisplay withMaterial(Material material) {
        this.material = material.block();
        if (cubeDisplay != null) {
            cubeDisplay.withMaterial(this.material);
        }
        return this;
    }

    @Override
    public CubeDisplay withGlowColor(@Nullable Color color) {
        this.glowColor = color;
        if (cubeDisplay != null) {
            cubeDisplay.withGlowColor(color);
        }
        return this;
    }

    @Override
    public void remove() {
        if (cubeDisplay != null) {
            cubeDisplay.remove();
        }
    }
}
