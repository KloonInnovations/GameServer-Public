package io.kloon.gameserver.service.allocations;

import io.kloon.bigbackend.transfers.TransferSlot;
import io.kloon.gameserver.allocation.AllocateSlotRequest;
import io.kloon.gameserver.service.allocations.approved.ApprovedTransfer;
import io.kloon.gameserver.service.allocations.approved.JoinApprovedTransfer;

import java.util.UUID;

public final class AllocatedSlot {
    private final UUID id;
    private final UUID minecraftUuid;
    private final UUID instanceId;

    private UUID joiningPlayerId;

    public AllocatedSlot(UUID id, UUID minecraftUuid, UUID instanceId) {
        this.id = id;
        this.minecraftUuid = minecraftUuid;
        this.instanceId = instanceId;
    }

    private AllocatedSlot(TransferSlot transferSlot) {
        this.id = transferSlot.slotId();
        this.minecraftUuid = transferSlot.minecraftUuid();
        this.instanceId = transferSlot.instanceId();
    }

    public UUID id() {
        return id;
    }

    public UUID minecraftUuid() {
        return minecraftUuid;
    }

    public UUID instanceId() {
        return instanceId;
    }

    public AllocatedSlot withJoining(UUID playerId) {
        this.joiningPlayerId = playerId;
        return this;
    }

    public UUID getJoiningPlayerId() {
        return joiningPlayerId;
    }

    public static AllocatedSlot fromRequest(AllocateSlotRequest request) {
        AllocatedSlot slot = new AllocatedSlot(request.slotId(), request.minecraftUuid(), request.instanceId());
        if (request.joining() != null) {
            slot.withJoining(request.joining().targetUuid());
        }
        return slot;
    }

    public static AllocatedSlot madeUp(ApprovedTransfer transfer) {
        TransferSlot transferSlot = transfer.getSlot();
        AllocatedSlot allocatedSlot = new AllocatedSlot(transferSlot);
        if (transfer instanceof JoinApprovedTransfer joining) {
            allocatedSlot.withJoining(joining.getJoinedPlayerId());
        }
        return allocatedSlot;
    }
}
