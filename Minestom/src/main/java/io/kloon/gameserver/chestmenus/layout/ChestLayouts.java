package io.kloon.gameserver.chestmenus.layout;

public final class ChestLayouts {
    private ChestLayouts() {}

    public static final ChestLayout INSIDE = new ChestLayout(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    );

    public static ChestLayout centeredSecondRowSpaced(int amount) {
        return switch (amount) {
            case 1 -> new ChestLayout(13);
            case 2 -> new ChestLayout(12, 14);
            case 3 -> new ChestLayout(11, 13, 15);
            case 4 -> new ChestLayout(10, 12, 14, 16);
            case 5 -> new ChestLayout(11, 12, 13, 14, 15);
            case 6 -> new ChestLayout(10, 11, 12, 14, 15, 16);
            default -> new ChestLayout(10, 11, 12, 13, 14, 15, 16);
        };
    }

    public static ChestLayout spaceOut8(int amount) {
        return switch (amount) {
            case 1 -> new ChestLayout(13);
            case 2 -> new ChestLayout(12, 14);
            case 3 -> new ChestLayout(11, 13, 15);
            case 4 -> new ChestLayout(10, 12, 14, 16);
            case 5 -> new ChestLayout(10, 12, 14, 16, 31);
            case 6 -> new ChestLayout(10, 12, 14, 16, 30, 32);
            case 7 -> new ChestLayout(10, 12, 14, 16, 29, 31, 33);
            default -> new ChestLayout(10, 12, 14, 16, 28, 30, 32, 34);
        };
    }
}
