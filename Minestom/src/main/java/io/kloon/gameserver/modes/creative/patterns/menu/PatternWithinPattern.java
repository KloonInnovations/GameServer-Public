package io.kloon.gameserver.modes.creative.patterns.menu;

import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.Material;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public record PatternWithinPattern<Pattern>(
        Material iconMat, String name,
        List<Component> lore,
        Function<Pattern, CreativePattern> get,
        BiFunction<Pattern, CreativePattern, Pattern> edit
) {
}
