package io.kloon.gameserver.modes.creative.masks.impl.inselection;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.modes.creative.masks.MaskType;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskWorkCache;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.util.physics.Collisions;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class InsideSelectionMask extends MaskType<InsideSelectionMask.Data> {
    public InsideSelectionMask(String dbKey) {
        super(dbKey, Data.class, Data::new);
    }

    @Override
    public ItemBuilder2 getIcon() {
        return new ItemBuilder2(Material.LEAD);
    }

    @Override
    public String getCommandLabel() {
        return "selection";
    }

    @Override
    public String getName() {
        return "Inside Selection";
    }

    @Override
    public Lore getDatalessDescription() {
        return new Lore().wrap("<gray>Block is inside your <selection<gray>.");
    }

    @Override
    public Lore getLore(Data data, boolean negated) {
        Lore lore = new Lore();
        if (negated) {
            lore.add("<gray>Block must NOT be inside <selection<gray>.");
        } else {
            lore.add("<gray>Block must be inside <selection><gray>.");
        }
        return lore;
    }

    @Override
    public Lore getConditionBulletPoint(Data data, boolean negated, boolean onlyPoint) {
        if (negated) {
            return new Lore().wrap(MM."\{PREFIX} Block outside <selection><gray>.");
        } else {
            return new Lore().wrap(MM."\{PREFIX} Block inside <selection><gray>.");
        }
    }

    @Override
    public boolean matches(MaskWorkCache workCache, Data data, Block.Getter instance, Point blockPos, Block block) {
        BoundingBox boundingBox = workCache.selectionBb();
        if (boundingBox == null) {
            return false;
        }

        return Collisions.containsExclusive(boundingBox, blockPos);
    }

    @Override
    public boolean skip(MaskWorkCache workCache, Data data, Block block) {
        return data.inactiveOnNoSelection && workCache.selectionBb() == null;
    }

    @Override
    public ChestMenu createMaskMenu(EditMaskItemMenu parent, MaskWithData<Data> mask) {
        return new InsideSelectionMenu(parent, mask);
    }

    public static class Data {
        private boolean inactiveOnNoSelection = true;

        public boolean isInactiveOnNoSelection() {
            return inactiveOnNoSelection;
        }

        public void setInactiveOnNoSelection(boolean inactiveOnNoSelection) {
            this.inactiveOnNoSelection = inactiveOnNoSelection;
        }
    }
}
