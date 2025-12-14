package io.kloon.gameserver.modes.creative.history.audit;

import io.kloon.gameserver.modes.creative.history.ChangeMeta;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.player.KloonPlayer;
import org.bson.types.ObjectId;

public record AuditRecord(
        long timestamp,
        ObjectId author,
        ChangeType type,
        ChangeMeta meta
) {
    public static AuditRecord create(KloonPlayer player, ChangeType type, ChangeMeta meta) {
        return new AuditRecord(System.currentTimeMillis(), player.getAccountId(), type, meta);
    }
}
