package io.kloon.gameserver.modes.creative.menu.preferences.numberinput;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.item.Material;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public record NumberInput<ItemBound>(
        Material iconMat, TextColor textColor,
        String name,
        List<Component> lore,
        double defaultValue, double min, double max,
        Function<ItemBound, Double> getValue,
        BiFunction<ItemBound, Double, ItemBound> editValue
) {
    public static <ItemBound> NumberInput<ItemBound> consumer(
            Material iconMat, TextColor textColor, String name,
            List<Component> lore,
            double defaultValue, double min, double max,
            Function<ItemBound, Double> getValue,
            BiConsumer<ItemBound, Double> setValue
    ) {
        return new NumberInput<>(iconMat, textColor, name,
                lore,
                defaultValue, min, max,
                getValue,
                (ItemBound itemBound, Double value) -> {
                    setValue.accept(itemBound, value);
                    return itemBound;
                });
    }

    public static <ItemBound> NumberInput<ItemBound> consumerInt(
            Material iconMat, TextColor textColor, String name,
            List<Component> lore,
            int defaultValue, int min, int max,
            Function<ItemBound, Integer> getValue,
            BiConsumer<ItemBound, Integer> setValue
    ) {
        return new NumberInput<>(iconMat, textColor, name,
                lore,
                defaultValue, min, max,
                itemBound -> (double) getValue.apply(itemBound),
                (ItemBound itemBound, Double value) -> {
                    setValue.accept(itemBound, value.intValue());
                    return itemBound;
                });
    }

    public static <ItemBound> NumberInput<ItemBound> functionInt(
            Material iconMat, TextColor textColor, String name,
            List<Component> lore,
            int defaultValue, int min, int max,
            Function<ItemBound, Integer> getValue,
            BiFunction<ItemBound, Integer, ItemBound> setValue
    ) {
        return new NumberInput<>(iconMat, textColor, name,
                lore,
                defaultValue, min, max,
                itemBound -> (double) getValue.apply(itemBound),
                (ItemBound itemBound, Double value) -> setValue.apply(itemBound, value.intValue()));
    }
}
