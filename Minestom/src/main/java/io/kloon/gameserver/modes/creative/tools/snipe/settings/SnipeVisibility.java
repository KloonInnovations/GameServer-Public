package io.kloon.gameserver.modes.creative.tools.snipe.settings;

import io.kloon.gameserver.chestmenus.listing.cycle.CycleLabelable;
import io.kloon.gameserver.util.WordUtilsK;
import io.kloon.infra.util.EnumQuery;
import net.minestom.server.color.Color;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

public enum SnipeVisibility implements CycleLabelable {
    GLOWING("glowing"),
    REGULAR("regular"),
    DESATURATED("desaturated"),
    INVISIBLE("invisible"),
    ;

    private final String dbKey;
    private final String label;

    SnipeVisibility(String dbKey) {
        this.dbKey = dbKey;
        this.label = WordUtilsK.enumName(this);
    }

    public String getDbKey() {
        return dbKey;
    }

    @Override
    public String label() {
        return label;
    }

    @Nullable
    public Color editGlow(Color color) {
        if (this == GLOWING) {
            return color;
        }
        return null;
    }

    @Nullable
    public Material editMat(Material material) {
        if (this == DESATURATED) {
            return Material.WHITE_STAINED_GLASS;
        }
        return material;
    }

    @Nullable
    public Block editMat(Block block) {
        if (this == DESATURATED) {
            return Block.WHITE_STAINED_GLASS;
        }
        return block;
    }

    public boolean isVisible() {
        return this != INVISIBLE;
    }

    public static final EnumQuery<String, SnipeVisibility> BY_DB_KEY = new EnumQuery<>(values(), s -> s.dbKey);
}
