package io.kloon.gameserver.creative.storage.saves;

import io.kloon.infra.util.cutenames.PetNames;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record WorldSave(
        ObjectId _id,
        ObjectId worldId,
        long timestamp,
        Integer version,
        Reason reason,
        @Nullable ObjectId loadedFrom,
        Integer indexInSession,
        String allocationId,
        String serverCuteName,
        @Nullable UUID instanceId,
        @Nullable ObjectId copiedFromSaveId
) {
    public static final int VERSION = 2;

    public String hexId() {
        return _id().toHexString();
    }

    public String cuteName() {
        return PetNames.generate(_id);
    }

    public Integer version() {
        return this.version == null ? 1 : this.version;
    }

    @Override
    public Integer indexInSession() {
        if (indexInSession == null) return 0;
        return indexInSession;
    }

    @Override
    public String allocationId() {
        if (allocationId == null) return "Unknown";
        return allocationId;
    }

    @Override
    public String serverCuteName() {
        if (allocationId == null) return "Unknown";
        return serverCuteName;
    }

    @Override
    public Reason reason() {
        if (reason == null) return Reason.UNKNOWN;
        return reason;
    }

    public enum Reason {
        UNKNOWN,
        INSTANCE_CLOSE,
        SERVER_CLOSE,
        AUTOSAVE,
        COMMAND,
        WORLD_COPIED
    }
}