package io.kloon.gameserver.modes.creative.tools.impl.layer.params;

import io.kloon.gameserver.chestmenus.listing.cycle.CycleLabelable;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.menu.CubeDimensionButton;
import io.kloon.gameserver.util.WordUtilsK;
import io.kloon.gameserver.util.coordinates.Axis;
import io.kloon.gameserver.util.coordinates.CardinalDirection;
import io.kloon.infra.util.EnumQuery;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.component.HeadProfile;
import org.jetbrains.annotations.Nullable;

public enum LayerAxis implements CycleLabelable {
    AUTO("auto"),
    X("x"),
    Y("y"),
    Z("z"),
    ;

    private final String dbKey;
    private final String label;

    LayerAxis(String dbKey) {
        this.dbKey = dbKey;
        this.label = WordUtilsK.enumName(this);
    }

    public String getDbKey() {
        return dbKey;
    }

    public Axis computeAxis(CreativePlayer player) {
        if (this == AUTO) {
            Vec dir = player.getLookVec();
            return CardinalDirection.closestDir(dir).axis();
        }
        return getAxis();
    }

    @Nullable
    public Axis getAxis() {
        return switch (this) {
            case X -> Axis.X;
            case Y -> Axis.Y;
            case Z -> Axis.Z;
            default -> null;
        };
    }

    public ItemBuilder2 getIcon() {
        if (this == AUTO) {
            return MenuStack.ofHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzZkZTMzMTQzODJiMjEwYjM0ZThjMWI1ZDI1YWU0Yjc1Yjg0OTA0NmU4ZWEwZWYwZTgwMGEzMjhiYWFiNWY3ZSJ9fX0=");
        }
        HeadProfile headProfile = CubeDimensionButton.getHead(getAxis());
        return MenuStack.ofHead(headProfile);
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public @Nullable String subLabel() {
        if (this == AUTO) {
            return "Based on look dir";
        }
        return null;
    }

    public static final EnumQuery<String, LayerAxis> BY_DB_KEY = new EnumQuery<>(values(), v -> v.dbKey);
}
