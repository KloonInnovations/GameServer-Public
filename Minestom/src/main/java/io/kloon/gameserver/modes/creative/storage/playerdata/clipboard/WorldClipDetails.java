package io.kloon.gameserver.modes.creative.storage.playerdata.clipboard;

import io.kloon.gameserver.util.mongo.MongoCodec;
import org.bson.Document;
import org.bson.types.ObjectId;

public record WorldClipDetails(
        String originWorldName,
        ObjectId originWorldId
) {

    public static final Codec CODEC = new Codec();
    public static final class Codec extends MongoCodec<WorldClipDetails> {
        @Override
        public void encodeInto(WorldClipDetails details, Document document) {
            document.put("origin_world_name", details.originWorldName());
            document.put("origin_world_id", details.originWorldId());
        }

        @Override
        public WorldClipDetails decode(Document document) {
            return new WorldClipDetails(
                    document.getString("origin_world_name"),
                    document.getObjectId("origin_world_id")
            );
        }
    }
}
