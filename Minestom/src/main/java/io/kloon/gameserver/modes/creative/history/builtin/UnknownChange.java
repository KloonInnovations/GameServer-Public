package io.kloon.gameserver.modes.creative.history.builtin;

import com.github.luben.zstd.Zstd;
import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.history.*;
import io.kloon.gameserver.modes.creative.history.results.ChangeResult;

import java.io.IOException;

public class UnknownChange implements Change {
    private final int id;
    private final byte[] compressedData;

    private UnknownChange(int id, byte[] compressedData) {
        this.id = id;
        this.compressedData = compressedData;
    }

    public static UnknownChange unknown(int id, byte[] data) {
        byte[] compressedData = Zstd.compress(data);
        return new UnknownChange(id, compressedData);
    }

    @Override
    public ChangeType getType() {
        return ChangeType.UNKNOWN;
    }

    @Override
    public ChangeResult undo(ChangeContext ctx) {
        return null;
    }

    @Override
    public ChangeResult redo(ChangeContext ctx) {
        return null;
    }

    public static final Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<UnknownChange> {
        @Override
        public void encode(UnknownChange change, MinecraftOutputStream out) throws IOException {
            out.writeVarInt(change.id);
            out.writeByteArray(change.compressedData);
        }

        @Override
        public UnknownChange decode(MinecraftInputStream in) throws IOException {
            int id = in.readVarInt();
            byte[] compressedData = in.readByteArray();
            return new UnknownChange(id, compressedData);
        }
    }
}
