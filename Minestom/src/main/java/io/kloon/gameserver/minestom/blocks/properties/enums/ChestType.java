package io.kloon.gameserver.minestom.blocks.properties.enums;

public enum ChestType {
    SINGLE,
    LEFT,
    RIGHT,
    ;

    public ChestType opposite() {
        return switch (this) {
            case SINGLE -> SINGLE;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}
