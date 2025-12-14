package io.kloon.gameserver.chestmenus;

import net.minestom.server.inventory.InventoryType;

public enum ChestSize {
    ONE(InventoryType.CHEST_1_ROW, 4),
    TWO(InventoryType.CHEST_2_ROW, 4),
    THREE(InventoryType.CHEST_3_ROW, 13),
    FOUR(InventoryType.CHEST_4_ROW, 13),
    FIVE(InventoryType.CHEST_5_ROW, 22),
    SIX(InventoryType.CHEST_6_ROW, 22),
    ;

    private final InventoryType inventoryType;
    private final int middleCenter;

    ChestSize(InventoryType inventoryType, int middleCenter) {
        this.inventoryType = inventoryType;
        this.middleCenter = middleCenter;
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }

    public int getMaxSlots() {
        return inventoryType.getSize();
    }

    public int minus(int slots) {
        return getMaxSlots() - 1 - slots;
    }

    public int last() {
        return getMaxSlots() - 1;
    }

    public int middleCenter() {
        return middleCenter;
    }

    public int bottomCenter() {
        return minus(4);
    }
}
