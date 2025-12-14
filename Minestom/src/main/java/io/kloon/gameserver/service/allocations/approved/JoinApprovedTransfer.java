package io.kloon.gameserver.service.allocations.approved;

import io.kloon.bigbackend.transfers.TransferSlot;

import java.util.UUID;

public class JoinApprovedTransfer extends ApprovedTransfer {
    private final UUID joinedPlayerId;

    public JoinApprovedTransfer(TransferSlot slot, UUID joinedPlayerId) {
        super(slot);
        this.joinedPlayerId = joinedPlayerId;
    }

    public UUID getJoinedPlayerId() {
        return joinedPlayerId;
    }
}
