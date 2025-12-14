package io.kloon.gameserver.modes.creative.tools.impl.laser.mode;

import io.kloon.gameserver.chestmenus.listing.cycle.CycleLabelable;
import io.kloon.gameserver.modes.creative.tools.impl.laser.mode.impl.CircleLaserMode;
import io.kloon.gameserver.modes.creative.tools.impl.laser.mode.impl.CubeLaserMode;
import io.kloon.gameserver.modes.creative.tools.impl.laser.mode.impl.SphereLaserMode;
import io.kloon.gameserver.modes.creative.tools.impl.laser.mode.impl.SquareLaserMode;
import io.kloon.infra.util.EnumQuery;

public enum LaserModeType implements CycleLabelable {
    CUBE("cube", "Cube", true),
    SPHERE("sphere", "Sphere", true),

    SQUARE("square", "Square", false),
    CIRCLE("circle", "Circle", false),
    ;

    private final String dbKey;
    private final String label;
    private final boolean threeDimensions;

    LaserModeType(String dbKey, String label, boolean threeDimensions) {
        this.dbKey = dbKey;
        this.label = label;
        this.threeDimensions = threeDimensions;
    }

    public String getDbKey() {
        return dbKey;
    }

    @Override
    public String label() {
        return label;
    }

    public boolean is3d() {
        return threeDimensions;
    }

    public LaserMode create() {
        return switch (this) {
            case CUBE -> new CubeLaserMode();
            case SPHERE -> new SphereLaserMode();
            case SQUARE -> new SquareLaserMode();
            case CIRCLE -> new CircleLaserMode();
        };
    }

    public static final EnumQuery<String, LaserModeType> BY_DB_KEY = new EnumQuery<>(LaserModeType.values(), l -> l.dbKey);
}
