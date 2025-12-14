package io.kloon.gameserver.modes.creative.tools.menus;

import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemRef {
    private final Supplier<ItemStack> get;
    private final Consumer<ItemStack> set;

    private ItemStack reference;

    public ItemRef(Supplier<ItemStack> get, Consumer<ItemStack> set) {
        this.get = get;
        this.set = set;
        this.reference = get.get();
    }

    public ItemStack getItem() {
        return reference;
    }

    // returns false if the item changed from the source
    public boolean setIfDidntChange(ItemStack item) {
        ItemStack itemNow = get.get();
        if (itemNow != reference) {
            return false;
        }

        set.accept(item);
        this.reference = item;

        return true;
    }

    public static ItemRef create(Supplier<ItemStack> get, Consumer<ItemStack> set) {
        return new ItemRef(get, set);
    }

    public static ItemRef mainHand(Player player) {
        return new ItemRef(player::getItemInMainHand, player::setItemInMainHand);
    }

    public static ItemRef slot(Player player, int slot) {
        return new ItemRef(() -> player.getInventory().getItemStack(slot), item -> player.getInventory().setItemStack(slot, item));
    }
}
