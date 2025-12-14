package io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.stored;

import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.WorldClip;
import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.WorldClipDetails;
import io.kloon.gameserver.util.mongo.MongoCodec;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;

public sealed interface StoredClipboardEntry permits CompressedClipboardEntry, NoClipboardEntry {
    @Nullable
    WorldClip toClip();

    Codec CODEC = new Codec();
    final class Codec extends MongoCodec<StoredClipboardEntry> {
        @Override
        public void encodeInto(StoredClipboardEntry entry, Document document) {
            switch (entry) {
                case CompressedClipboardEntry compressed -> {
                    document.put("_id", compressed.id());
                    document.put("details", WorldClipDetails.CODEC.encode(compressed.details()));
                    document.put("volume_size", compressed.volumeSize());
                    document.put("volume_compressed", compressed.compressedVolume());
                }
                case NoClipboardEntry none -> {}
            }
        }

        @Override
        public StoredClipboardEntry decode(Document document) {
            if (document.containsKey("_id")) {
                ObjectId id = document.getObjectId("_id");
                Document detailsDoc = document.get("details", Document.class);
                WorldClipDetails details = WorldClipDetails.CODEC.decode(detailsDoc);
                int volumeSize = document.getInteger("volume_size");
                byte[] compressedVolume = readBinary(document, "volume_compressed");
                return new CompressedClipboardEntry(id, details, volumeSize, compressedVolume);
            }
            return new NoClipboardEntry();
        }
    }
}
