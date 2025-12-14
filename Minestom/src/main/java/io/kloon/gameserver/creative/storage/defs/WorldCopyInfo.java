package io.kloon.gameserver.creative.storage.defs;

import io.kloon.gameserver.creative.storage.owner.WorldOwnerStorage;
import io.kloon.infra.util.codecs.MongoJackCodec;
import org.bson.types.ObjectId;

public record WorldCopyInfo(
        long timestamp,
        ObjectId worldId,
        String worldName,
        WorldOwnerStorage originalOwner,
        ObjectId saveId
) {
    public static final MongoJackCodec<WorldCopyInfo> BSON_CODEC = new MongoJackCodec<>(WorldCopyInfo.class);
}
