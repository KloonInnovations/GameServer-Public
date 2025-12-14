package io.kloon.gameserver.minestom;

import io.kloon.gameserver.minestom.armor.ArmorSlot;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class InventoryExtras {
    private final Player player;
    private final PlayerInventory inventory;

    public InventoryExtras(Player player) {
        this.player = player;
        this.inventory = player.getInventory();
    }

    public boolean grab(ItemStack item) {
        byte heldSlot = player.getHeldSlot();

        ItemStack itemInHeldSlot = inventory.getItemStack(heldSlot);
        if (itemInHeldSlot.isAir()) {
            inventory.setItemStack(heldSlot, item);
            return true;
        }

        return inventory.addItemStack(item);
    }

    public int getFreeSlots() {
        int free = 0;
        for (int slot = 0; slot < inventory.getInnerSize(); ++slot) {
            if (inventory.getItemStack(slot).isAir()) {
                ++free;
            }
        }
        return free;
    }

    public boolean hasFreeSlots() {
        return getFreeSlots() > 0;
    }

    public boolean isFull() {
        return !hasFreeSlots();
    }

    public int getSlot(ItemStack item) {
        if (item == null || item.isAir()) return -1;
        for (int slot = 0; slot < inventory.getSize(); ++slot) {
            ItemStack inSlot = inventory.getItemStack(slot);
            if (item.equals(inSlot)) {
                return slot;
            }
        }
        return -1;
    }

    // true if added
    public boolean addItemReverse(ItemStack item) {
        for (int slot = inventory.getInnerSize() - 1; slot >= 0; --slot) {
            ItemStack inSlot = inventory.getItemStack(slot);
            if (inSlot.isAir()) {
                inventory.setItemStack(slot, item);
                return true;
            }
        }
        return false;
    }

    // returns the slot, or -1 if none found
    public int addToHotbar(ItemStack item) {
        PlayerInventory inv = player.getInventory();
        for (int slot = 0; slot < 9; ++slot) {
            ItemStack inSlot = inv.getItemStack(slot);
            if (inSlot.isAir()) {
                inv.setItemStack(slot, item);
                return slot;
            }
        }
        return -1;
    }

    @Nullable
    public ArmorSlot addToArmor(ItemStack item) {
        for (ArmorSlot slot : ArmorSlot.VALUES) {
            ItemStack existing = slot.get(player);
            if (existing.isAir()) {
                slot.set(player, item);
                return slot;
            }
        }
        return null;
    }

    @Nullable
    public ArmorSlot getFreeArmorSlot() {
        for (ArmorSlot slot : ArmorSlot.VALUES) {
            ItemStack item = slot.get(player);
            if (item.isAir()) {
                return slot;
            }
        }
        return null;
    }

    public static void consumeItemInMainHand(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) return;

        ItemStack inHand = player.getItemInMainHand();
        if (inHand.amount() > 1) {
            inHand = inHand.withAmount(inHand.amount() - 1);
        } else {
            inHand = ItemStack.AIR;
        }
        player.setItemInMainHand(inHand);
    }
}
