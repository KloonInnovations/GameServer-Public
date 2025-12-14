package io.kloon.gameserver.minestom.blocks.vanilla.tiles.impl.campfire;

import io.kloon.gameserver.Kgs;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.ItemStack;

import java.util.List;

public enum CampfireCorner { // xz based on Facing = West
    NORTH_EAST(1, -1),
    SOUTH_EAST(1, 1),
    SOUTH_WEST(-1, 1),
    NORTH_WEST(-1, -1),
    ;

    private final Vec direction;

    CampfireCorner(int x, int z) {
        this.direction = new Vec(x, 0, z);
    }

    public Vec getDirection() {
        return direction;
    }

    public int getSlot() {
        return ordinal();
    }

    public ItemStack get(List<ItemStack> items) {
        int index = getSlot();
        if (index >= items.size()) {
            return ItemStack.AIR;
        }
        ItemStack stack = items.get(index);
        return stack == null ? ItemStack.AIR : stack;
    }

    public void set(List<ItemStack> items, ItemStack item) {
        if (items.size() < 4) {
            int missing = 4 - items.size();
            for (int i = 0; i < missing; ++i) {
                items.add(ItemStack.AIR);
            }
        }
        items.set(getSlot(), item);
    }
}
