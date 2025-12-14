package io.kloon.gameserver.modes.creative.tools;

import io.kloon.gameserver.util.WordUtilsK;
import net.kyori.adventure.text.Component;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public enum CreativeToolCategory {
    SYSTEM,
    ESSENTIAL,
    GENERAL,
    SELECTION,
    MOVEMENT,
    SHAPE,
    TERRAFORMING
    ;

    private final String name;

    CreativeToolCategory() {
        this.name = WordUtilsK.enumName(this);
    }

    public Component getLoreLine() {
        return MM."<dark_gray>\{name} Tool";
    }
}
