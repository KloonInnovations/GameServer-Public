package io.kloon.gameserver.chestmenus;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChestMenuInv extends Inventory {
    private final ChestMenu menu;

    public ChestMenuInv(@NotNull InventoryType inventoryType, @NotNull Component title, ChestMenu menu) {
        super(inventoryType, title);
        this.menu = menu;
    }

    public ChestMenuInv(@NotNull InventoryType inventoryType, @NotNull String title, ChestMenu menu) {
        super(inventoryType, title);
        this.menu = menu;
    }

    public ChestMenu getMenu() {
        return menu;
    }

    public static boolean hasOpen(Player player, Class<?> menuClass) {
        if (!player.isOnline()) return false;

        AbstractInventory openInv = player.getOpenInventory();
        return openInv instanceof ChestMenuInv chestInv
                && menuClass.isInstance(chestInv.getMenu());
    }

    @Nullable
    public static ChestMenuInv get(Player player) {
        if (player == null || !player.isOnline()) return null;

        AbstractInventory openInv = player.getOpenInventory();
        return openInv instanceof ChestMenuInv chestInv ? chestInv : null;
    }

    @Nullable
    public static ChestMenu getOpenMenu(Player player) {
        if (!player.isOnline()) return null;

        AbstractInventory openInv = player.getOpenInventory();
        return openInv instanceof ChestMenuInv chestInv ? chestInv.getMenu() : null;
    }

    public static void rerender(Player player) {
        if (!(player.getOpenInventory() instanceof ChestMenuInv chestMenuInv)) return;
        ChestMenu menu = chestMenuInv.getMenu();
        menu.reload();
        menu.render(chestMenuInv, player);
    }

    public static void rerenderButton(int slot, Player player) {
        if (!(player.getOpenInventory() instanceof ChestMenuInv chestMenuInv)) return;
        ChestMenu menu = chestMenuInv.getMenu();
        ChestButton button = menu.getButton(slot);
        if (button == null) return;
        ItemStack renderedItem = menu.render(slot, button, player);
        chestMenuInv.setItemStack(slot, renderedItem);
    }

    public static void rerenderButton(int slot, Player player, ChestMenu expectedMenu) {
        if (!(player.getOpenInventory() instanceof ChestMenuInv chestMenuInv)) return;
        ChestMenu menu = chestMenuInv.getMenu();
        if (expectedMenu != menu) return;
        ChestButton button = menu.getButton(slot);
        if (button == null) return;
        ItemStack renderedItem = menu.render(slot, button, player);
        chestMenuInv.setItemStack(slot, renderedItem);
    }

    public static void sendTitle(Player player) {
        AbstractInventory openInv = player.getOpenInventory();
        if (openInv instanceof ChestMenuInv chestInv) {
            Component title = chestInv.getMenu().generateTitle(player);
            chestInv.setTitle(title);
        }
    }
}
