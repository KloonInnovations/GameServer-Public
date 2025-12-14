package io.kloon.gameserver.modes.creative.network;

import io.kloon.gameserver.GameServerTopics;
import io.kloon.gameserver.KloonGameServer;
import io.kloon.gameserver.games.CreateCreativeInstanceReply;
import io.kloon.gameserver.games.CreateCreativeInstanceRequest;
import io.kloon.gameserver.modes.creative.CreativeMode;
import io.kloon.infra.serviceframework.NatsService;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Instance;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class CreativeService extends NatsService {
    private static final Logger LOG = LoggerFactory.getLogger(CreativeService.class);

    private final CreativeMode creative;

    public CreativeService(KloonGameServer gameServer, CreativeMode creative) {
        super(gameServer.getInfra().nats(), MinecraftServer.getSchedulerManager());
        this.creative = creative;

        String thisServer = gameServer.getAllocName();
        subscribeJsonRpcAsync(GameServerTopics.CREATE_CREATIVE_INSTANCE.toServer(thisServer), CreateCreativeInstanceRequest.class, this::createCreativeInstance);
    }

    public CompletableFuture<CreateCreativeInstanceReply> createCreativeInstance(CreateCreativeInstanceRequest request) {
        try {
            ObjectId worldId = new ObjectId(request.worldIdHex());

            CompletableFuture<? extends Instance> createInstance;
            if (request.saveIdHex() == null) {
                createInstance = creative.createInstanceLatestSave(worldId);
                LOG.info(STR."Received creative latest-save instance creation request \{request}");
            } else {
                ObjectId saveId = new ObjectId(request.saveIdHex());
                createInstance = creative.createInstanceSpecificSave(worldId, saveId);
                LOG.info(STR."Received creative specific-save instance creation request \{request}");
            }

            return createInstance.thenApply(instance -> {
                LOG.info(STR."Created creative instance for request \{request}");
                return new CreateCreativeInstanceReply(instance.getUniqueId(), true);
            }).exceptionally(t -> {
                LOG.error(STR."Error creating creative instance from \{request}", t);
                return new CreateCreativeInstanceReply(null, false);
            });
        } catch (Throwable t) {
            LOG.error(STR."Error creating creative instance from \{request}", t);
            return CompletableFuture.completedFuture(new CreateCreativeInstanceReply(null, false));
        }
    }
}
