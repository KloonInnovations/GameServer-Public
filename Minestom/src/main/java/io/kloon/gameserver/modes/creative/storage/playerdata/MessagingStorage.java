package io.kloon.gameserver.modes.creative.storage.playerdata;

import io.kloon.gameserver.modes.creative.ux.messaging.MessagingState;
import io.kloon.infra.mongo.storage.BufferedDocument;

public class MessagingStorage {
    private final BufferedDocument document;

    public static final MessagingState DEFAULT_SELF = MessagingState.CHAT;
    public static final MessagingState DEFAULT_OTHERS = MessagingState.ACTION_BAR;

    public MessagingStorage(BufferedDocument document) {
        this.document = document;
    }

    public MessagingState getSelf() {
        String stateStr = document.getString(SELF, DEFAULT_SELF.getDbKey());
        return MessagingState.BY_DB_KEY.get(stateStr, DEFAULT_SELF);
    }

    public void setSelf(MessagingState state) {
        document.putString(SELF, state.getDbKey());
    }

    public MessagingState getOthers() {
        String stateStr = document.getString(OTHERS, DEFAULT_OTHERS.getDbKey());
        return MessagingState.BY_DB_KEY.get(stateStr, DEFAULT_OTHERS);
    }

    public void setOthers(MessagingState state) {
        document.putString(OTHERS, state.getDbKey());
    }

    public boolean isActionBarQueueEnabled() {
        return document.getBoolean(ACTION_BAR_QUEUING, true);
    }

    public void setActionBarQueuingEnabled(boolean enabled) {
        document.putBoolean(ACTION_BAR_QUEUING, enabled);
    }

    private static final String SELF = "self";
    private static final String OTHERS = "others";
    private static final String ACTION_BAR_QUEUING = "action_bar_queue";
}
