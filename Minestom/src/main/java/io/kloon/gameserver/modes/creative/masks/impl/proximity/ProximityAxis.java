package io.kloon.gameserver.modes.creative.masks.impl.proximity;

import io.kloon.gameserver.chestmenus.listing.cycle.CycleLabelable;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.menu.CubeDimensionButton;
import io.kloon.gameserver.util.WordUtilsK;
import io.kloon.gameserver.util.coordinates.Axis;
import io.kloon.infra.util.EnumQuery;
import net.minestom.server.item.component.HeadProfile;
import org.jetbrains.annotations.Nullable;

public enum ProximityAxis implements CycleLabelable {
    ALL("all"),
    AUTO("auto"),
    X("x"),
    Y("y"),
    Z("z"),
    ;

    private final String dbKey;
    private final String name;

    ProximityAxis(String dbKey) {
        this.dbKey = dbKey;
        this.name = WordUtilsK.enumName(this);
    }

    public String getDbKey() {
        return dbKey;
    }

    @Override
    public String label() {
        return name;
    }

    public Axis getAxis() {
        return switch (this) {
            case X -> Axis.X;
            case Y -> Axis.Y;
            case Z -> Axis.Z;
            default -> throw new IllegalStateException("No axis");
        };
    }

    @Override
    public @Nullable String subLabel() {
        return switch (this) {
            case ALL -> "3d";
            case AUTO -> "Based on look dir";
            default -> null;
        };
    }

    public ItemBuilder2 getIcon() {
        if (this == ALL) {
            return MenuStack.ofHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWRkMjJkYjhjNmUyMzhmYjhjYzA4MTlkMDJhNjU0MDMyOTdkNjNiNjdjNmM3Y2U2YjQzYmM4MjkxODk4MzdmNCJ9fX0=");
        }
        if (this == AUTO) {
            return MenuStack.ofHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzZkZTMzMTQzODJiMjEwYjM0ZThjMWI1ZDI1YWU0Yjc1Yjg0OTA0NmU4ZWEwZWYwZTgwMGEzMjhiYWFiNWY3ZSJ9fX0=");
        }
        HeadProfile headProfile = CubeDimensionButton.getHead(getAxis());
        return MenuStack.ofHead(headProfile);
    }

    public static final EnumQuery<String, ProximityAxis> BY_DB_KEY = new EnumQuery<>(values(), t -> t.dbKey);
}
