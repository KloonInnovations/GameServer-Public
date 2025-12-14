package io.kloon.gameserver.modes.creative.tools.impl.layer.params;

import io.kloon.gameserver.chestmenus.listing.cycle.CycleLabelable;
import io.kloon.gameserver.util.WordUtilsK;
import io.kloon.infra.util.EnumQuery;

public enum LayerShape implements CycleLabelable {
    SQUARE("square"),
    CIRCLE("circle"),
    ;

    private final String dbKey;
    private final String label;

    LayerShape(String dbKey) {
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

    public static final EnumQuery<String, LayerShape> BY_DB_KEY = new EnumQuery<>(values(), l -> l.dbKey);
}
