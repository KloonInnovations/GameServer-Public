package io.kloon.gameserver.modes.creative.masks;

import io.kloon.infra.util.EnumQuery;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.item.Material;

public enum MasksUnion {
    OR("or", "Any", NamedTextColor.BLUE, Material.TORCHFLOWER),
    AND("and", "All", TextColor.color(247, 148, 56), Material.TORCHFLOWER_SEEDS),
    ;

    private final String dbKey;
    private final String name;
    private final TextColor color;
    private final Material icon;

    MasksUnion(String dbKey, String name, TextColor color, Material icon) {
        this.dbKey = dbKey;
        this.name = name;
        this.color = color;
        this.icon = icon;
    }

    public String getDbKey() {
        return dbKey;
    }

    public String getName() {
        return name;
    }

    public String getNameMM() {
        return STR."<\{color.asHexString()}>\{name}";
    }

    public Material getIcon() {
        return icon;
    }

    public MasksUnion toggle() {
        return switch (this) {
            case OR -> AND;
            case AND -> OR;
        };
    }

    public static final EnumQuery<String, MasksUnion> BY_DB_KEY = new EnumQuery<>(values(), m -> m.dbKey);
}
