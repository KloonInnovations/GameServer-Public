package io.kloon.gameserver.modes.creative.ux.sidebar.title;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RotatingGradient {
    private final List<RainbowRotation> colors = new ArrayList<>();

    public RotatingGradient(int anchors) {
        for (int i = 0; i < anchors; ++i) {
            colors.add(new RainbowRotation());
        }
    }

    public String createPart(String text) {
        String colorsFmt = colors.stream()
                .map(c -> c.generateColor().asHexString())
                .collect(Collectors.joining(":"));

        return STR."<gradient:\{colorsFmt}>\{text}</gradient>";
    }
}
