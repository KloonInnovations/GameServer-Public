package io.kloon.gameserver.client;

import io.kloon.gameserver.GameServerTopics;
import io.kloon.gameserver.tablist.TablistState;
import io.kloon.gameserver.tablist.TablistUpdate;
import io.kloon.infra.serviceframework.NatsClient;
import io.nats.client.Connection;

public class GlobalServerClient extends NatsClient {
    public GlobalServerClient(Connection nats) {
        super(nats);
    }

    public void broadcastTablist(TablistState state) {
        publishJson(GameServerTopics.TABLIST_STATE, state);
    }

    public void broadcastTablistChange(TablistUpdate update) {
        publishJson(GameServerTopics.TABLIST_UPDATE, update);
    }
}
