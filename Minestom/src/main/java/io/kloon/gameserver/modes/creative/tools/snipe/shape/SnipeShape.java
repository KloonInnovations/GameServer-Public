package io.kloon.gameserver.modes.creative.tools.snipe.shape;

import io.kloon.gameserver.chestmenus.listing.cycle.CycleLabelable;
import io.kloon.gameserver.modes.creative.tools.snipe.quick.CubeDisplay;
import io.kloon.gameserver.modes.creative.tools.snipe.quick.NoDisplay;
import io.kloon.gameserver.modes.creative.tools.snipe.quick.QuickDisplay;
import io.kloon.gameserver.modes.creative.tools.snipe.quick.SphereDisplay;
import io.kloon.gameserver.util.WordUtilsK;
import io.kloon.infra.util.EnumQuery;
import net.minestom.server.instance.Instance;

import java.util.Arrays;
import java.util.List;

public enum SnipeShape implements CycleLabelable {
    NONE("none"),
    CUBE("cube"),
    SPHERE("sphere"),
    ;

    private final String dbKey;
    private final String name;

    SnipeShape(String dbKey) {
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

    public QuickDisplay createQuickDisplay(Instance instance) {
        return switch (this) {
            case NONE -> new NoDisplay();
            case CUBE -> new CubeDisplay(instance);
            case SPHERE -> new SphereDisplay(instance);
        };
    }

    public static final EnumQuery<String, SnipeShape> BY_DB_KEY = new EnumQuery<>(values(), t -> t.dbKey);
    public static final List<SnipeShape> LIST_WITHOUT_NONE = Arrays.stream(values()).filter(v -> v != NONE).toList();
}
