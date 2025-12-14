package io.kloon.gameserver.allocation;

import java.util.concurrent.CompletableFuture;

public class AllocateSlotReply {
    private final boolean allocated;
    private final Status status;

    public AllocateSlotReply(boolean allocated, Status status) {
        this.allocated = allocated;
        this.status = status;
    }

    public boolean isAllocated() {
        return allocated;
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {
        OK,
        DENIED_CUSTOM,
        SERVER_IS_FULL,
        JOIN_PLAYER_NOT_ON_INSTANCE,
        JOIN_PLAYER_HAS_DISABLED_THE_SETTING,
        JOIN_WORLD_IS_DISABLED,
        TARGET_BLOCKED_REQUESTER,
        INSTANCE_NOT_FOUND
        ;

        public AllocateSlotReply asReply() {
            boolean allocated = this == OK;
            return new AllocateSlotReply(allocated, this);
        }

        public CompletableFuture<AllocateSlotReply> asReplyFuture() {
            return CompletableFuture.completedFuture(asReply());
        }
    }
}
