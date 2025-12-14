package io.kloon.gameserver.modes.creative.menu.preferences.toggles;

import io.kloon.gameserver.modes.creative.storage.datainworld.CreativeWorldStorage;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public record WorldStorageToggle(
        Material iconMat, String iconText, String name,
        List<Component> lore, @Nullable String commandLabel,
        Function<CreativeWorldStorage, Boolean> isEnabled,
        BiConsumer<CreativeWorldStorage, Boolean> setEnabled
) {
}
