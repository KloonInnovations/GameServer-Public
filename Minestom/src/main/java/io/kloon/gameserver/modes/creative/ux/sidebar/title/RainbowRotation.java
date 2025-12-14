package io.kloon.gameserver.modes.creative.ux.sidebar.title;

import io.kloon.gameserver.minestom.GlobalMinestomTicker;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;

import java.util.concurrent.ThreadLocalRandom;

public class RainbowRotation {
    private final long OFFSET = ThreadLocalRandom.current().nextLong(30_000);

    public String createPart(String text) {
        TextColor color = generateColor();
        String hexString = color.asHexString();
        return STR."<\{hexString}>\{text}</\{hexString}>";
    }

    public TextColor generateColor() {
        double seconds = (GlobalMinestomTicker.getTick() + OFFSET) / 20.0;

        double h = ((seconds * 6) % 360) / 360;

        double svVar = 15;
        double up = 1 - 1 / svVar;

        double s = Math.cos(seconds * Math.PI * 2 / 26) / svVar + up;
        double v = Math.cos((seconds + 27) * Math.PI * 2 / 127) / svVar + up;

        return TextColor.color(HSVLike.hsvLike((float) h, (float) s, (float) v));
    }
}
