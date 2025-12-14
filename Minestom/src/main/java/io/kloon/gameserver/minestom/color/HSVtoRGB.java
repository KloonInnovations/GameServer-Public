package io.kloon.gameserver.minestom.color;

import net.kyori.adventure.util.HSVLike;
import net.minestom.server.color.Color;

public class HSVtoRGB {
    public static Color convert(HSVLike hsv) {
        float h = hsv.h() * 360;
        float s = hsv.s();
        float v = hsv.v();

        float r, g, b;

        int i = (int) Math.floor(h / 60.0f) % 6;
        float f = (h / 60.0f) - i;
        float p = v * (1 - s);
        float q = v * (1 - s * f);
        float t = v * (1 - s * (1 - f));

        switch (i) {
            case 0:
                r = v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = v;
                b = p;
                break;
            case 2:
                r = p;
                g = v;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = v;
                break;
            case 4:
                r = t;
                g = p;
                b = v;
                break;
            case 5:
            default:
                r = v;
                g = p;
                b = q;
                break;
        }

        return new Color(
                Math.round(r * 255),
                Math.round(g * 255),
                Math.round(b * 255)
        );
    }
}
