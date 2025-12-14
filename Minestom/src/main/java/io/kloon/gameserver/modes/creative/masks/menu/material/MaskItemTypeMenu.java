package io.kloon.gameserver.modes.creative.masks.menu.material;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.armor.ArmorFamily;
import io.kloon.gameserver.minestom.armor.ArmorSlot;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class MaskItemTypeMenu extends ChestMenu {
    private final EditMaskItemMenu parent;

    public MaskItemTypeMenu(EditMaskItemMenu parent) {
        super("Mask Armor Piece", ChestSize.SIX);
        this.parent = parent;
    }

    @Override
    protected void registerButtons() {
        ArmorFamily[] families = ArmorFamily.values();
        ArmorSlot[] slots = ArmorSlot.values();
        for (int i = 0; i < families.length; ++i) {
            ArmorFamily armorFamily = families[i];
            int topSlot = 11 + i;
            for (int j = 0; j < slots.length; ++j) {
                ArmorSlot armorSlot = slots[j];
                int slot = topSlot + j * 9;
                Material material = armorFamily.get(armorSlot);
                reg(slot, new MaskItemTypeButton(parent, material));
            }
        }

        reg().goBack(parent);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Mask Armor Piece";

        Lore lore = new Lore();
        lore.add("<dark_gray>Mask Item");
        lore.addEmpty();
        lore.wrap("<gray>Change which material this mask item has, maybe to a different slot!");
        lore.addEmpty();
        lore.add("<cta>Click to pick material!");

        return MenuStack.of(Material.ARMOR_STAND, name, lore);
    }
}
