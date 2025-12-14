package io.kloon.gameserver.modes.creative.menu.preferences.toggles;

import io.kloon.gameserver.modes.creative.storage.playerdata.CreativePlayerStorage;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public record PlayerStorageToggle(
        Material iconMat, @Nullable String iconText, String name,
        List<Component> lore, @Nullable String commandLabel,
        Function<CreativePlayerStorage, Boolean> isEnabled,
        BiConsumer<CreativePlayerStorage, Boolean> setEnabled
) {
}
