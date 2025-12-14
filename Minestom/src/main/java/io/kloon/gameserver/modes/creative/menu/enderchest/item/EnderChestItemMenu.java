package io.kloon.gameserver.modes.creative.menu.enderchest.item;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.enderchest.EnderChestMenu;
import io.kloon.gameserver.modes.creative.storage.enderchest.EnderChestItem;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

public class EnderChestItemMenu extends ChestMenu {
    private final EnderChestMenu parent;
    private final EnderChestItem item;

    public EnderChestItemMenu(EnderChestMenu parent, EnderChestItem item) {
        super("Ender Chest Item", ChestSize.FOUR);
        this.parent = parent;
        this.item = item;
    }

    @Override
    protected void registerButtons() {
        reg(11, new GrabEnderChestItemButton(item));
        reg(15, new DeleteEnderChestItemButton(item));
        reg().goBack(parent);
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        if (click.isRightClick()) {
            super.clickButton(p, click);
            return;
        }

        CreativePlayer player = (CreativePlayer) p;
        GrabEnderChestItemButton.copyToInventory(player, item);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Lore lore = new Lore();
        lore.add("<rcta>Click to manage!");
        lore.add("<lcta>Click to copy this item!");

        return MenuStack.extraLore(item.getItemStack(), lore).build();
    }
}
