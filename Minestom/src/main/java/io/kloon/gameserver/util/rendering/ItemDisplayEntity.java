package io.kloon.gameserver.util.rendering;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import org.jetbrains.annotations.NotNull;

public class ItemDisplayEntity extends Entity {
    public ItemDisplayEntity() {
        super(EntityType.ITEM_DISPLAY);

        ItemDisplayMeta meta = getEntityMeta();
        meta.setHasNoGravity(true);
    }

    @Override
    public @NotNull ItemDisplayMeta getEntityMeta() {
        return (ItemDisplayMeta) super.getEntityMeta();
    }

    @Override
    public boolean preventBlockPlacement() {
        return false;
    }
}
