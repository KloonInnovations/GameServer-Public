package io.kloon.gameserver.modes;

import io.kloon.infra.facts.KloonEnvironment;
import io.kloon.infra.util.EnumQuery;
import org.jetbrains.annotations.Nullable;

public enum ModeType {
    HUB("hub"),
    CREATIVE("creative"),
    DEV("dev"),
    ;

    private final String modeKey;

    ModeType(String modeKey) {
        this.modeKey = modeKey;
    }

    public String getDbKey() {
        return modeKey;
    }

    public boolean canBootOn(KloonEnvironment env) {
        if (this == DEV) {
            return env == KloonEnvironment.DEV;
        }
        return true;
    }

    @Nullable
    public static ModeType parse(String modeKey) {
        return BY_MODEKEY.get(modeKey);
    }

    public static ModeType parse(String modeKey, ModeType def) {
        if (modeKey == null) return def;
        return BY_MODEKEY.get(modeKey, def);
    }

    public static final EnumQuery<String, ModeType> BY_MODEKEY = new EnumQuery<>(ModeType.values(), m -> m.modeKey);
}
