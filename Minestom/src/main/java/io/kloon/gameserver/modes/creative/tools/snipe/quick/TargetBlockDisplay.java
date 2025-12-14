package io.kloon.gameserver.modes.creative.tools.snipe.quick;

import io.kloon.gameserver.modes.creative.tools.snipe.ToolSnipe;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TargetBlockDisplay extends Entity {
    private final ToolSnipe<?> snipe;

    private static final Vec SIZE = new Vec(1.1);

    public TargetBlockDisplay(ToolSnipe<?> snipe) {
        super(EntityType.ITEM_DISPLAY);
        this.snipe = snipe;

        ItemDisplayMeta meta = getEntityMeta();
        meta.setItemStack(ItemStack.of(Material.RED_STAINED_GLASS));
        meta.setHasNoGravity(true);
        meta.setPosRotInterpolationDuration(1);
        meta.setScale(SIZE);

        position = computePos();
    }

    public TargetBlockDisplay withMaterial(Material material) {
        getEntityMeta().setItemStack(ItemStack.of(material));
        return this;
    }

    public TargetBlockDisplay withGlowColor(@Nullable Color color) {
        ItemDisplayMeta meta = getEntityMeta();
        if (color == null) {
            meta.setHasGlowingEffect(false);
        } else {
            meta.setGlowColorOverride(color.asRGB());
            meta.setHasGlowingEffect(true);
        }
        return this;
    }

    @Override
    public void update(long time) {
        if (!snipe.isValid()) {
            remove();
            return;
        }

        Pos pos = computePos();
        if (!getPosition().equals(pos)) {
            teleport(pos);
        }
    }

    @Override
    public @NotNull ItemDisplayMeta getEntityMeta() {
        return (ItemDisplayMeta) super.getEntityMeta();
    }

    private Pos computePos() {
        Pos pos = Pos.fromPoint(snipe.getTarget());
        return pos.add(0.5);
    }
}
