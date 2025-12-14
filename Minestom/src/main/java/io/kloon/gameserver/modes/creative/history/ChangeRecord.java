package io.kloon.gameserver.modes.creative.history;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.builtin.UnknownChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public record ChangeRecord(
        long startTimestamp,
        long endTimestamp,
        UUID author,
        ChangeMeta meta,
        Change change
) {
    public ChangeRecord {
        if (meta == null) {
            throw new IllegalArgumentException("ChangeMeta cannot be null");
        }
    }

    public static ChangeRecord instant(CreativePlayer player, ChangeMeta meta, Change change) {
        long timestamp = System.currentTimeMillis();
        return new ChangeRecord(timestamp, timestamp, player.getUuid(), meta, change);
    }

    public static final Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<ChangeRecord> {
        private static final Logger CODEC_LOG = LoggerFactory.getLogger(Codec.class);

        @Override
        public void encode(ChangeRecord record, MinecraftOutputStream out) throws IOException {
            Change change = record.change;
            ChangeType type = change.getType();
            out.writeVarInt(type.getDbKey());

            out.writeLong(record.startTimestamp);
            out.writeLong(record.endTimestamp);
            out.writeUuid(record.author);

            MinecraftCodec codec = type.getCodec();

            out.write(record.meta, ChangeMeta.CODEC);

            byte[] changeBytes = MinecraftOutputStream.toBytes(change, codec);
            //CODEC_LOG.info("encoded " + change.getType() + " with " + changeBytes.length);
            out.writeByteArray(changeBytes);
        }

        @Override
        public ChangeRecord decode(MinecraftInputStream in) throws IOException {
            int typeId = in.readVarInt();
            ChangeType type = ChangeType.byDbKey(typeId);

            try {
                long startTimestamp = in.readLong();
                long endTimestamp = in.readLong();
                UUID author = in.readUuid();

                ChangeMeta meta = in.read(ChangeMeta.CODEC);

                byte[] changeData = in.readByteArray();
                //CODEC_LOG.info("decoded " + type + " with " + changeData.length);

                Change change;
                if (type == null) {
                    change = UnknownChange.unknown(typeId, changeData);
                } else {
                    change = type.getCodec().decode(new MinecraftInputStream(changeData));
                }

                if (change == null) {
                    throw new IOException(STR."Change is null for type \{type}");
                }

                return new ChangeRecord(startTimestamp, endTimestamp, author, meta, change);
            } catch (Throwable t) {
                throw new RuntimeException(STR."Error decoding change of type \{type}", t);
            }
        }
    }
}
