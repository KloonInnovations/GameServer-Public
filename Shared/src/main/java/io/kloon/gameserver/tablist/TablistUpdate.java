package io.kloon.gameserver.tablist;

import java.util.UUID;

public class TablistUpdate {
    private final UUID uuid;
    private final Action action;

    public TablistUpdate(UUID uuid, Action action) {
        this.uuid = uuid;
        this.action = action;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Action getAction() {
        return action;
    }

    public enum Action {
        ADD,
        REMOVE
    }
}