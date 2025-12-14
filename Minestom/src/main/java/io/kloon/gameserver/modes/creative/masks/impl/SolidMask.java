package io.kloon.gameserver.modes.creative.masks.impl;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.modes.creative.masks.MaskType;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskWorkCache;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.modes.creative.masks.impl.SolidMask.*;

public class SolidMask extends MaskType<Data> {
    public SolidMask(String dbKey) {
        super(dbKey, Data.class, Data::new);
    }

    @Override
    public ItemBuilder2 getIcon() {
        return new ItemBuilder2(Material.POLISHED_BLACKSTONE_SLAB);
    }

    @Override
    public String getName() {
        return "Solid";
    }

    @Override
    public Lore getDatalessDescription() {
        return new Lore().wrap("<gray>Block is solid, meaning you cannot walk through it.");
    }

    @Override
    public Lore getLore(Data data, boolean negated) {
        Lore lore = new Lore();
        if (negated) {
            lore.add("<gray>Block is NOT solid.");
        } else {
            lore.add("<gray>Block is solid.");
        }
        return lore;
    }

    @Override
    public Lore getConditionBulletPoint(Data data, boolean negated, boolean onlyPoint) {
        if (negated) {
            return new Lore().wrap(MM."\{PREFIX} Block is NOT solid.");
        } else {
            return new Lore().wrap(MM."\{PREFIX} Block is solid.");
        }
    }

    @Override
    public boolean matches(MaskWorkCache workCache, Data data, Block.Getter instance, Point blockPos, Block block) {
        return block.isSolid();
    }

    public static class Data {}
}
