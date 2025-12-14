package io.kloon.gameserver.modes.creative.storage.datainworld.waypoints;

import io.kloon.infra.util.EnumQuery;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.color.Color;
import net.minestom.server.item.Material;
import org.apache.commons.text.WordUtils;

import java.util.*;

public enum WaypointColor {
    WHITE("white", NamedTextColor.WHITE, Material.WHITE_BANNER),
    SILVER("silver", NamedTextColor.GRAY, Material.LIGHT_GRAY_BANNER),
    GRAY("gray", NamedTextColor.DARK_GRAY, Material.GRAY_BANNER),
    BLACK("black", NamedTextColor.BLACK, Material.BLACK_BANNER),
    BROWN("brown", TextColor.color(96, 62, 37), Material.BROWN_BANNER),
    RED("red", NamedTextColor.RED, Material.RED_BANNER),
    ORANGE("orange", NamedTextColor.GOLD, Material.ORANGE_BANNER),
    YELLOW("yellow", NamedTextColor.YELLOW, Material.YELLOW_BANNER),
    LIME("lime", NamedTextColor.GREEN, Material.LIME_BANNER),
    GREEN("green", NamedTextColor.DARK_GREEN, Material.GREEN_BANNER),
    CYAN("cyan", NamedTextColor.DARK_AQUA, Material.CYAN_BANNER),
    AQUA("aqua", NamedTextColor.AQUA, Material.LIGHT_BLUE_BANNER),
    BLUE("blue", NamedTextColor.BLUE, Material.BLUE_BANNER),
    PURPLE("magenta", TextColor.color(145, 57, 137), Material.PURPLE_BANNER),
    MAGENTA("purple", TextColor.color(145, 57, 138), Material.MAGENTA_BANNER),
    PINK("pink", NamedTextColor.LIGHT_PURPLE, Material.PINK_BANNER)
    ;

    private final String dbKey;
    private final String name;
    private final TextColor textColor;
    private final Material material;

    WaypointColor(String dbKey, TextColor textColor, Material material) {
        this.dbKey = dbKey;
        this.name = WordUtils.capitalize(dbKey);
        this.textColor = textColor;
        this.material = material;
    }

    public String getDbKey() {
        return dbKey;
    }

    public String getName() {
        return name;
    }

    public TextColor getTextColor() {
        return textColor;
    }

    public Material getMaterial() {
        return material;
    }

    public Color getColor() {
        return new Color(textColor.red(), textColor.green(), textColor.blue());
    }

    public static final List<WaypointColor> LIST = Arrays.asList(values());
    public static final Set<WaypointColor> SET = Set.copyOf(LIST);
    public static final EnumQuery<String, WaypointColor> BY_DBKEY = new EnumQuery<>(WaypointColor.values(), w -> w.dbKey);
}
