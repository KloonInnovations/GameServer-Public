package io.kloon.gameserver.modes.creative.menu.preferences.numberinput.legacy;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public record NumberInput(
        Material iconMat, TextColor textColor, @Nullable String iconText,
        String name,
        List<Component> lore,
        @Nullable String commandLabel, @Nullable ToolDataType dataType,
        double defaultValue, double min, double max,
        Function<CreativePlayer, Double> getValue,
        BiConsumer<CreativePlayer, Double> setValue
) {

}
