package io.kloon.gameserver.modes.creative.masks.impl;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.modes.creative.masks.MaskType;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskWorkCache;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.modes.creative.masks.impl.OverlayTypeMask.*;

public class OverlayTypeMask extends MaskType<Data> {
    public OverlayTypeMask(String dbKey) {
        super(dbKey, Data.class, Data::new);
    }

    @Override
    public ItemBuilder2 getIcon() {
        return new ItemBuilder2(Material.BIG_DRIPLEAF);
    }

    @Override
    public String getName() {
        return "Overlay";
    }

    @Override
    public Lore getDatalessDescription() {
        return new Lore().wrap("<gray>Block has <white>AIR <gray>above.");
    }

    @Override
    public Lore getLore(Data data, boolean negated) {
        Lore lore = new Lore();
        if (negated) {
            lore.add("<gray>Block must NOT have <white>AIR <gray>above it.");
        } else {
            lore.add("<gray>Block must have <white>AIR <gray>above it.");
        }
        return lore;
    }

    @Override
    public Lore getConditionBulletPoint(Data data, boolean negated, boolean onlyPoint) {
        if (negated) {
            return new Lore().wrap(MM."\{PREFIX} Block above is NOT <white>AIR<gray>.");
        } else {
            return new Lore().wrap(MM."\{PREFIX} Block has <white>AIR <gray>above it.");
        }
    }

    @Override
    public boolean matches(MaskWorkCache workCache, Data data, Block.Getter instance, Point blockPos, Block block) {
        Block above = instance.getBlock(blockPos.relative(BlockFace.TOP));
        return above.isAir();
    }

    public static class Data {

    }
}
