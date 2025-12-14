package io.kloon.gameserver.service;

import io.kloon.gameserver.GameServerTopics;
import io.kloon.gameserver.KloonGameServer;
import io.kloon.gameserver.allocation.AllocateSlotReply;
import io.kloon.gameserver.allocation.AllocateSlotRequest;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.player.settings.menu.GeneralSettingsMenu;
import io.kloon.gameserver.service.allocations.AllocatedSlot;
import io.kloon.gameserver.service.allocations.AllocationSlotsListener;
import io.kloon.gameserver.service.allocations.SlotAllocationEvent;
import io.kloon.infra.serviceframework.NatsService;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class SpecificServerService extends NatsService {
    private static final Logger LOG = LoggerFactory.getLogger(SpecificServerService.class);

    private final KloonGameServer gameServer;

    public SpecificServerService(KloonGameServer gameServer) {
        super(gameServer.getInfra().nats(), MinecraftServer.getSchedulerManager());
        this.gameServer = gameServer;

        String thisServer = gameServer.getAllocName();
        subscribeJsonRpcAsync(GameServerTopics.ALLOCATE_SLOT.toServer(thisServer), AllocateSlotRequest.class, this::handleSlotRequest);
    }

    public CompletableFuture<AllocateSlotReply> handleSlotRequest(AllocateSlotRequest request) {
        AllocationSlotsListener allocations = gameServer.getAllocationSlotsListener();

        Instance instance = gameServer.getInstanceManager().getInstance(request.instanceId());
        if (instance == null) {
            LOG.info(STR."Attempted to allocate a slot on non-existant instance \{request.instanceId()}");
            return AllocateSlotReply.Status.INSTANCE_NOT_FOUND.asReplyFuture();
        }

        int occupiedSlots = getOccupiedSlots(instance);
        int maxSlots = gameServer.getMode().getMaxPlayers();
        if (occupiedSlots >= maxSlots) {
            LOG.info(STR."Attempted to allocate a slot on instance with (\{occupiedSlots}/\{maxSlots}) slots");
            return AllocateSlotReply.Status.SERVER_IS_FULL.asReplyFuture();
        }

        SlotAllocationEvent event = new SlotAllocationEvent(instance, request);
        EventDispatcher.call(event);
        if (event.isCancelled()) {
            LOG.info(STR."Slot allocation request \{request} denied by event");
            return event.getDenyStatus().asReplyFuture();
        }

        CompletableFuture<AllocateSlotReply.Status> getAsyncStatus;
        if (request.joining() == null) {
            getAsyncStatus = CompletableFuture.completedFuture(null);
        } else {
            Entity joinTargetEntity = instance.getEntityByUuid(request.joining().targetUuid());
            if (!(joinTargetEntity instanceof KloonPlayer joinTarget)) {
                return AllocateSlotReply.Status.JOIN_PLAYER_NOT_ON_INSTANCE.asReplyFuture();
            }
            if (!joinTarget.isEnabled(GeneralSettingsMenu.ACCEPT_JOINS)) {
                return AllocateSlotReply.Status.JOIN_PLAYER_HAS_DISABLED_THE_SETTING.asReplyFuture();
            }
            getAsyncStatus = computeAsyncStatus(request, joinTarget);
        }

        return getAsyncStatus.thenApplyAsync(asyncStatus -> {
            if (asyncStatus != null) {
                return asyncStatus.asReply();
            }

            AllocatedSlot slot = AllocatedSlot.fromRequest(request);
            allocations.register(slot);
            return AllocateSlotReply.Status.OK.asReply();
        }, executor);
    }

    private CompletableFuture<AllocateSlotReply.Status> computeAsyncStatus(AllocateSlotRequest request, KloonPlayer joinTarget) {
        return joinTarget.hasBlocked(request.accountId()).thenApply(isBlocked -> {
            LOG.info("isBlocked: " + isBlocked);
            return isBlocked ? AllocateSlotReply.Status.TARGET_BLOCKED_REQUESTER : null;
        });
    }

    private int getOccupiedSlots(Instance instance) {
        ConnectionManager connMan = MinecraftServer.getConnectionManager();
        AllocationSlotsListener allocations = gameServer.getAllocationSlotsListener();

        int playersOnInstance = instance.getPlayers().size();
        int playersInConfigurationState = connMan.getConfigPlayers().size();
        int pendingAllocPlayers = allocations.getAllocatedCount(instance);
        return playersOnInstance + playersInConfigurationState + pendingAllocPlayers;
    }
}
