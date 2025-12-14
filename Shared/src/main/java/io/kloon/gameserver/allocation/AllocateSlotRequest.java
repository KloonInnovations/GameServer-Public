package io.kloon.gameserver.allocation;

import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class AllocateSlotRequest {
    private final UUID slotId;
    private final String accountIdHex;
    private final UUID minecraftUuid;
    private final UUID instanceId;

    private PlayerJoin join;

    public AllocateSlotRequest(UUID slotId, ObjectId accountId, UUID minecraftUuid, UUID instanceId) {
        this.slotId = slotId;
        this.accountIdHex = accountId.toHexString();
        this.minecraftUuid = minecraftUuid;
        this.instanceId = instanceId;
    }

    public UUID slotId() {
        return slotId;
    }

    public ObjectId accountId() {
        return new ObjectId(accountIdHex);
    }

    public UUID minecraftUuid() {
        return minecraftUuid;
    }

    public UUID instanceId() {
        return instanceId;
    }

    public AllocateSlotRequest withJoin(PlayerJoin join) {
        this.join = join;
        return this;
    }

    @Nullable
    public PlayerJoin joining() {
        return join;
    }

    public record PlayerJoin(UUID targetUuid) {}

    @Override
    public String toString() {
        return "AllocateSlotRequest{" +
               "slotId=" + slotId +
               ", accountIdHex='" + accountIdHex + '\'' +
               ", minecraftUuid=" + minecraftUuid +
               ", instanceId=" + instanceId +
               ", join=" + join +
               '}';
    }
}
