package io.kloon.gameserver.creative.storage.owner;

import io.kloon.gameserver.creative.storage.owner.player.PlayerWorldOwner;
import io.kloon.gameserver.player.KloonPlayer;
import org.bson.Document;
import org.bson.types.ObjectId;

public record WorldOwnerStorage(
        ObjectId playerId
) {
    public WorldOwner toOwner() {
        return new PlayerWorldOwner(playerId);
    }

    public boolean isOwner(KloonPlayer player) {
        return player.getAccountId().equals(playerId);
    }

    public boolean isOwner(ObjectId accountId) {
        return playerId.equals(accountId);
    }

    public static final Codec BSON_CODEC = new Codec();
    public static class Codec implements io.kloon.infra.util.codecs.Codec<WorldOwnerStorage, Document> {
        @Override
        public Document encode(WorldOwnerStorage worldOwnership) {
            return new Document()
                    .append("playerId", worldOwnership.playerId);
        }

        @Override
        public WorldOwnerStorage decode(Document document) {
            return new WorldOwnerStorage(
                    document.getObjectId("playerId")
            );
        }
    }
}
