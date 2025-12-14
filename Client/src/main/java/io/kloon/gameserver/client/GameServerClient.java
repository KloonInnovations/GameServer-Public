package io.kloon.gameserver.client;

import io.kloon.gameserver.GameServerTopics;
import io.kloon.gameserver.allocation.AllocateSlotReply;
import io.kloon.gameserver.allocation.AllocateSlotRequest;
import io.kloon.gameserver.games.CreateCreativeInstanceReply;
import io.kloon.gameserver.games.CreateCreativeInstanceRequest;
import io.kloon.infra.serviceframework.NatsClient;
import io.nats.client.Connection;

import java.util.concurrent.CompletableFuture;

public class GameServerClient extends NatsClient {
    private final String allocationName;

    public GameServerClient(Connection nats, String allocationName) {
        super(nats);
        this.allocationName = allocationName;
    }

    public CompletableFuture<AllocateSlotReply> allocateSlot(AllocateSlotRequest request) {
        return requestJson(GameServerTopics.ALLOCATE_SLOT.toServer(allocationName), request, AllocateSlotReply.class);
    }

    public CompletableFuture<CreateCreativeInstanceReply> createCreativeInstance(CreateCreativeInstanceRequest request) {
        return requestJson(GameServerTopics.CREATE_CREATIVE_INSTANCE.toServer(allocationName), request, CreateCreativeInstanceReply.class);
    }
}
