package io.kloon.gameserver.modes.creative.ux.messaging;

import io.kloon.gameserver.chestmenus.listing.cycle.CycleLabelable;
import io.kloon.infra.util.EnumQuery;

public enum MessagingState implements CycleLabelable {
    CHAT("chat", "Chat"),
    ACTION_BAR("action_bar", "Action Bar"),
    MUTED("muted", "Muted"),
    ;

    private final String dbKey;
    private final String label;

    MessagingState(String dbKey, String label) {
        this.dbKey = dbKey;
        this.label = label;
    }

    public String getDbKey() {
        return dbKey;
    }

    @Override
    public String label() {
        return label;
    }

    public static final EnumQuery<String, MessagingState> BY_DB_KEY = new EnumQuery<>(values(), v -> v.dbKey);
}