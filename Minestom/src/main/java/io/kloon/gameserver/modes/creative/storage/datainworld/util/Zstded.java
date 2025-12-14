package io.kloon.gameserver.modes.creative.storage.datainworld.util;

import com.github.luben.zstd.Zstd;
import io.kloon.gameserver.minestom.io.*;

import java.io.IOException;

public class Zstded<T> {
    private final byte[] compressed;
    private final int originalSize;

    public Zstded(T obj, MinecraftEncoder<T> encoder) {
        try {
            byte[] encoded = MinecraftOutputStream.toBytes(obj, encoder);
            this.originalSize = encoded.length;
            this.compressed = Zstd.compress(encoded);
        } catch (Throwable t) {
            throw new RuntimeException("Error", t);
        }
    }

    public Zstded(byte[] compressed, int originalSize) {
        this.compressed = compressed;
        this.originalSize = originalSize;
    }

    public T read(MinecraftDecoder<T> decoder) throws IOException {
        byte[] decompressed = Zstd.decompress(compressed, originalSize);
        return decoder.decode(new MinecraftInputStream(decompressed));
    }

    public static final Codec CODEC = new Codec();
    public static final class Codec implements MinecraftCodec<Zstded> {
        @Override
        public void encode(Zstded obj, MinecraftOutputStream out) throws IOException {
            out.writeByteArray(obj.compressed);
            out.writeVarInt(obj.originalSize);
        }

        @Override
        public Zstded decode(MinecraftInputStream in) throws IOException {
            return new Zstded(
                    in.readByteArray(),
                    in.readVarInt()
            );
        }
    }
}
