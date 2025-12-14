package io.kloon.gameserver.minestom.utils;

import net.minestom.server.color.Color;

public final class ColorFmt {
    private ColorFmt() {}

    // https://stackoverflow.com/questions/3607858/convert-a-rgb-color-value-to-a-hexadecimal-string
    public static String toHex(Color color) {
        return String.format("#%02X%02X%02X", color.red(), color.green(), color.blue());
    }
}
