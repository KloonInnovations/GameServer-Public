package io.kloon.gameserver.creative.storage.deletion;

import io.kloon.infra.util.codecs.MongoJackCodec;
import org.bson.types.ObjectId;

public record WorldDeletion(
        ObjectId playerId,
        long timestamp
) {
    public static final MongoJackCodec<WorldDeletion> BSON_CODEC = new MongoJackCodec<>(WorldDeletion.class);
}
