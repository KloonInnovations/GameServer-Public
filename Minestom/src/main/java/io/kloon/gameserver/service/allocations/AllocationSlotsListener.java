package io.kloon.gameserver.service.allocations;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
import io.kloon.bigbackend.transfers.TransferSlot;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.backend.GameServerInfo;
import io.kloon.gameserver.minestom.events.EventHandler;
import io.kloon.gameserver.service.allocations.approved.ApprovedTransfer;
import io.kloon.velocity.mc.LoginPluginChannels;
import io.kloon.velocity.mc.transfers.TransferSlotIdReply;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class AllocationSlotsListener {
    private static final Logger LOG = LoggerFactory.getLogger(AllocationSlotsListener.class);

    private final Cache<UUID, AllocatedSlot> allocatedSlotsById = Caffeine.newBuilder()
            .expireAfterWrite(4, TimeUnit.SECONDS)
            .build();

    private final InstanceManager instanceManager;
    private final GameServerInfo serverInfo;

    public AllocationSlotsListener(InstanceManager instanceManager, GameServerInfo serverInfo) {
        this.instanceManager = instanceManager;
        this.serverInfo = serverInfo;
    }

    public void register(AllocatedSlot slot) {
        allocatedSlotsById.put(slot.id(), slot);
        LOG.info(STR."Registered \{slot}");
    }

    public int getAllocatedCount(Instance instance) {
        allocatedSlotsById.cleanUp();
        return (int) allocatedSlotsById.asMap().values().stream()
                .filter(slot -> slot.instanceId().equals(instance.getUniqueId()))
                .count();
    }

    public AllocatedSlot grabSlot(UUID slotId) {
        AllocatedSlot slot = allocatedSlotsById.getIfPresent(slotId);
        allocatedSlotsById.invalidate(slotId);
        return slot;
    }

    @EventHandler
    public void onLoginStart(AsyncPlayerPreLoginEvent event) {
        GameProfile profile = event.getGameProfile();
        PlayerConnection connection = event.getConnection();

        CompletableFuture<AllocatedSlot> getAllocatedSlot = event.sendPluginRequest(LoginPluginChannels.REQUEST_TRANSFER_SLOT_ID, new byte[0]).thenApply(response -> {
            byte[] payload = response.payload();
            if (payload == null || payload.length == 0) {
                throw new RuntimeException(STR."The proxy didn't understand our \{response.channel()} request for \{profile}");
            }

            String json = new String(payload);
            TransferSlotIdReply transferSlotIdReply = new Gson().fromJson(json, TransferSlotIdReply.class);
            AllocatedSlot slot = grabSlot(transferSlotIdReply.slotId());
            if (slot == null) {
                LOG.warn(STR."Unknown allocated slot id \{transferSlotIdReply.slotId()} from proxy for \{profile}");
            }
            return slot;
        }).orTimeout(3, TimeUnit.SECONDS);

        Kgs.INSTANCE.getFirstConfigProcessor().runAsyncAfterLogin(connection, "fetch allocation slot", (player, configEvent) -> {
            AllocatedSlot slot = getAllocatedSlot.join();

            if (slot == null) {
                player.kick(MM."<red>The instance <white>\{serverInfo.allocationName()} <red>is not ready to accept your connection.");
                return;
            }

            LOG.info(STR."Assigned allocated slot for \{player}: \{slot}");

            Instance instance = instanceManager.getInstance(slot.instanceId());
            if (instance == null) {
                player.kick(MM."<red>The instance you were intended to move to doesn't exist anymore.");
                return;
            }

            configEvent.setSpawningInstance(instance);
            player.scheduleNextTick(_ -> postTransfer(player, slot));
        });
    }

    public boolean isLocalTransfer(ApprovedTransfer transfer) {
        return isLocalTransfer(transfer.getSlot());
    }

    public boolean isLocalTransfer(TransferSlot slot) {
        return Kgs.getInfra().allocationName().equals(slot.serverAllocation());
    }

    public void execLocalTransfer(KloonPlayer player, ApprovedTransfer transfer) {
        TransferSlot slot = transfer.getSlot();
        LOG.info(STR."Executing local transfer for \{player} to slot \{slot}");
        Instance instance = instanceManager.getInstance(slot.instanceId());
        if (instance == null) {
            LOG.error(STR."Instance not found on local transfer \{slot}");
            player.sendMessage(MM."<red>The instance wasn't found on this server!");
            return;
        }

        player.closeInventory();
        player.setInstance(instance);

        AllocatedSlot allocatedSlot = AllocatedSlot.madeUp(transfer);
        player.scheduleNextTick(_ -> postTransfer(player, allocatedSlot));
    }

    private void postTransfer(KloonPlayer player, AllocatedSlot slot) {
        EventDispatcher.call(new TransferSlotUsedEvent(player, slot));
    }
}
