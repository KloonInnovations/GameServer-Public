package io.kloon.gameserver.modes.creative.tools.menus.toggles;

import net.kyori.adventure.text.Component;
import net.minestom.server.item.Material;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public record ToolToggle<T>(
        Material icon, String name,
        List<Component> lore,
        Function<T, Boolean> isEnabled,
        BiFunction<T, Boolean, T> editEnabled
) {
    public ToolToggle(
        Material icon, String name,
        List<Component> lore,
        Function<T, Boolean> isEnabled,
        BiConsumer<T, Boolean> setEnabled
    ) {
        this(icon, name, lore, isEnabled,
                (T data, Boolean enabled) -> {
                    setEnabled.accept(data, enabled);
                    return data;
                });
    }
}
