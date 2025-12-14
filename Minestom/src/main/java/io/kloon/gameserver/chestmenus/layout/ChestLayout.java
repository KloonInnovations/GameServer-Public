package io.kloon.gameserver.chestmenus.layout;

import java.util.List;

public class ChestLayout {
    private final int[] slots;

    public ChestLayout(int... slots) {
        this.slots = slots;
    }

    public <T> void distribute(List<T> list, LayoutDistributor<T> distributor) {
        for (int i = 0; i < Math.min(list.size(), slots.length); ++i) {
            int slot = slots[i];
            T item = list.get(i);
            distributor.consume(slot, item);
        }
    }

    public int size() {
        return slots.length;
    }

    public interface LayoutDistributor<T> {
        void consume(int slot, T item);
    }
}
