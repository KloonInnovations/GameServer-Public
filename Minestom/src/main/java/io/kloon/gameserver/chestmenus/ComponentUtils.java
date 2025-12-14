package io.kloon.gameserver.chestmenus;

import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

public final class ComponentUtils {
    public static int pixelWidth(char c) {
        if (Character.isUpperCase(c)) {
            return c == 'I' ? 3 : 5;
        } else if (Character.isDigit(c)) {
            return 5;
        } else if (Character.isLowerCase(c)) {
            return switch (c) {
                case 'i' -> 1;
                case 'l' -> 2;
                case 't' -> 3;
                case 'f', 'k' -> 4;
                default -> 5;
            };
        } else {
            return switch (c) {
                case '!', '.', ',', ';', ':', '|' -> 1;
                case '\'' -> 2;
                case '[', ']', ' ' -> 3;
                case '*', '(', ')', '{', '}', '<', '>' -> 4;
                case '@' -> 6;
                default -> 5;
            };
        }
    }
}
