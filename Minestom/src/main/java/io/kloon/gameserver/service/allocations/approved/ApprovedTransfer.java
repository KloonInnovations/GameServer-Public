package io.kloon.gameserver.service.allocations.approved;

import io.kloon.bigbackend.transfers.TransferSlot;

public class ApprovedTransfer {
    protected final TransferSlot slot;

    public ApprovedTransfer(TransferSlot slot) {
        this.slot = slot;
    }

    public TransferSlot getSlot() {
        return slot;
    }
}
