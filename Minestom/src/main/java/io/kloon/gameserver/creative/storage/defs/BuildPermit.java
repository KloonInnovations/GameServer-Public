package io.kloon.gameserver.creative.storage.defs;

import io.kloon.gameserver.modes.creative.buildpermits.duration.EphemeralPermit;
import io.kloon.gameserver.modes.creative.buildpermits.duration.InfinitePermit;
import io.kloon.gameserver.modes.creative.buildpermits.duration.PermitDuration;
import io.kloon.gameserver.modes.creative.buildpermits.duration.TimedPermit;
import io.kloon.infra.util.codecs.MongoJackCodec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record BuildPermit(
        ObjectId accountId,
        UUID minecraftUuid,
        ObjectId issuedBy,
        UUID issuedByUuid,
        long issueTimestamp,
        long durationMs
) {
    public boolean isExpired(@Nullable Instance instance) {
        PermitDuration duration = PermitDuration.fromMs(durationMs);
        return switch (duration) {
            case EphemeralPermit _ -> {
                if (instance == null) yield true;
                Player issuer = instance.getPlayerByUuid(issuedByUuid);
                yield issuer == null || !issuer.isOnline();
            }
            case InfinitePermit _ -> false;
            case TimedPermit timed -> System.currentTimeMillis() > timed.expiryMs(issueTimestamp);
        };
    }

    public PermitDuration duration() {
        return PermitDuration.fromMs(durationMs);
    }

    public static final MongoJackCodec<BuildPermit> BSON_CODEC = new MongoJackCodec<>(BuildPermit.class);
}
