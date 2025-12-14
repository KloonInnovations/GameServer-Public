package io.kloon.gameserver;

import io.kloon.infra.serviceframework.NatsTopic;

public final class GameServerTopics {
    private GameServerTopics() {}

    public static final NatsTopic ALLOCATE_SLOT = new NatsTopic("gs.allocate_slot");

    public static final NatsTopic CREATE_CREATIVE_INSTANCE = new NatsTopic("gs.creative.create_instance");

    public static final String TABLIST_UPDATE = "gs.tablist.update";
    public static final String TABLIST_STATE = "gs.tablist.state";
}
