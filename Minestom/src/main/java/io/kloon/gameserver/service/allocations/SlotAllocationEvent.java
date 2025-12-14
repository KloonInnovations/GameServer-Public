package io.kloon.gameserver.service.allocations;

import io.kloon.gameserver.allocation.AllocateSlotReply;
import io.kloon.gameserver.allocation.AllocateSlotRequest;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlotAllocationEvent implements InstanceEvent, CancellableEvent {
    private static final Logger LOG = LoggerFactory.getLogger(SlotAllocationEvent.class);

    private final Instance instance;
    private final AllocateSlotRequest request;

    private boolean cancelled;
    private AllocateSlotReply.Status denyStatus = AllocateSlotReply.Status.DENIED_CUSTOM;
    private String denyReason = "Unknown";

    public SlotAllocationEvent(Instance instance, AllocateSlotRequest request) {
        this.instance = instance;
        this.request = request;
    }

    @Override
    public @NotNull Instance getInstance() {
        return instance;
    }

    public AllocateSlotRequest getRequest() {
        return request;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public void setDeny(AllocateSlotReply.Status status, String reason) {
        setCancelled(true);
        this.denyStatus = status;
        this.denyReason = reason;
    }

    public AllocateSlotReply.Status getDenyStatus() {
        return denyStatus;
    }

    public String getDenyReason() {
        return denyReason;
    }
}
