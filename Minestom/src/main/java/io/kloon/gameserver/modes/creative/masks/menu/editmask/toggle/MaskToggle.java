package io.kloon.gameserver.modes.creative.masks.menu.editmask.toggle;

import net.kyori.adventure.text.Component;
import net.minestom.server.item.Material;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public record MaskToggle<T>(
        Material icon, String name,
        List<Component> lore,
        Function<T, Boolean> isEnabled,
        BiConsumer<T, Boolean> setEnabled
) {
}
