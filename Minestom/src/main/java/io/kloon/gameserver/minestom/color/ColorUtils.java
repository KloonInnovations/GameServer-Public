package io.kloon.gameserver.minestom.color;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.color.Color;
import net.minestom.server.item.Material;

import java.util.*;

public final class ColorUtils {
    private ColorUtils() {}

    public static RGBLike closestRGB(RGBLike color, Collection<? extends RGBLike> possibilities) {
        RGBLike closest = possibilities.stream()
                .min(Comparator.comparingDouble(possibility -> computeRgbDistanceSquared(color, possibility)))
                .orElse(null);
        return closest == null ? color : closest;
    }

    public static double computeRgbDistanceSquared(RGBLike a, RGBLike b) {
        double dr = Math.abs(b.red() - a.red());
        double rg = Math.abs(b.green() - a.green());
        double rb = Math.abs(b.blue() - a.blue());
        return dr*dr + rg*rg + rb*rb;
    }

    public static Color hsvToColor(HSVLike hsv) {
        return HSVtoRGB.convert(hsv);
    }

    public static final List<NamedTextColor> NAMED = Arrays.asList(
            NamedTextColor.BLACK,
            NamedTextColor.DARK_BLUE,
            NamedTextColor.DARK_GREEN,
            NamedTextColor.DARK_AQUA,
            NamedTextColor.DARK_RED,
            NamedTextColor.DARK_PURPLE,
            NamedTextColor.GOLD,
            NamedTextColor.GRAY,
            NamedTextColor.DARK_GRAY,
            NamedTextColor.BLUE,
            NamedTextColor.GREEN,
            NamedTextColor.AQUA,
            NamedTextColor.RED,
            NamedTextColor.LIGHT_PURPLE,
            NamedTextColor.YELLOW,
            NamedTextColor.WHITE
    );

    public static final BiMap<Material, RGBLike> CONCRETE_COLORS = HashBiMap.create();
    static {
        CONCRETE_COLORS.put(Material.RED_CONCRETE, TextColor.color(123, 28, 26));
        CONCRETE_COLORS.put(Material.ORANGE_CONCRETE, TextColor.color(213, 91, 16));
        CONCRETE_COLORS.put(Material.YELLOW_CONCRETE, TextColor.color(235, 172, 29));
        CONCRETE_COLORS.put(Material.LIME_CONCRETE, TextColor.color(82, 158, 25));
        CONCRETE_COLORS.put(Material.GREEN_CONCRETE, TextColor.color(59, 76, 27));
        CONCRETE_COLORS.put(Material.CYAN_CONCRETE, TextColor.color(21, 102, 123));
        CONCRETE_COLORS.put(Material.LIGHT_BLUE_CONCRETE, TextColor.color(34, 121, 185));
        CONCRETE_COLORS.put(Material.BLUE_CONCRETE, TextColor.color(36, 35, 126));
        CONCRETE_COLORS.put(Material.PURPLE_CONCRETE, TextColor.color(86, 24, 139));
        CONCRETE_COLORS.put(Material.MAGENTA_CONCRETE, TextColor.color(152, 40, 141));
        CONCRETE_COLORS.put(Material.PINK_CONCRETE, TextColor.color(203, 91, 131));
        CONCRETE_COLORS.put(Material.BROWN_CONCRETE, TextColor.color(82, 47, 24));
        CONCRETE_COLORS.put(Material.GRAY_CONCRETE, TextColor.color(42, 46, 49));
        CONCRETE_COLORS.put(Material.LIGHT_GRAY_CONCRETE, TextColor.color(113, 113, 102));
        CONCRETE_COLORS.put(Material.WHITE_CONCRETE, TextColor.color(205, 212, 212));
        CONCRETE_COLORS.put(Material.BLACK_CONCRETE, TextColor.color(6, 5, 8));
    }

    public static Material closestConcrete(RGBLike color) {
        RGBLike closestColor = closestRGB(color, CONCRETE_COLORS.values());
        return CONCRETE_COLORS.inverse().get(closestColor);
    }

    public static final EnumMap<BossBar.Color, TextColor> BOSS_BAR_TO_TEXT_COLOR = new EnumMap<>(BossBar.Color.class);
    static {
        BOSS_BAR_TO_TEXT_COLOR.put(BossBar.Color.PINK, NamedTextColor.LIGHT_PURPLE);
        BOSS_BAR_TO_TEXT_COLOR.put(BossBar.Color.BLUE, NamedTextColor.AQUA);
        BOSS_BAR_TO_TEXT_COLOR.put(BossBar.Color.RED, NamedTextColor.RED);
        BOSS_BAR_TO_TEXT_COLOR.put(BossBar.Color.GREEN, NamedTextColor.GREEN);
        BOSS_BAR_TO_TEXT_COLOR.put(BossBar.Color.YELLOW, NamedTextColor.YELLOW);
        BOSS_BAR_TO_TEXT_COLOR.put(BossBar.Color.PURPLE, NamedTextColor.DARK_PURPLE);
        BOSS_BAR_TO_TEXT_COLOR.put(BossBar.Color.WHITE, NamedTextColor.WHITE);
    }
}
