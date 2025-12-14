package io.kloon.gameserver.modes.creative.storage.datainworld.minestom;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;

import java.io.IOException;

public record StorageBlockVec(int x, int y, int z) {
    public StorageBlockVec(Point point) {
        this(point.blockX(), point.blockY(), point.blockZ());
    }

    public BlockVec toBlockVec() {
        return new BlockVec(x, y, z);
    }

    public Pos toPos() {
        return new Pos(x, y, z);
    }

    public static final Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<StorageBlockVec> {
        @Override
        public void encode(StorageBlockVec vec, MinecraftOutputStream out) throws IOException {
            out.writeVarInt(vec.x());
            out.writeVarInt(vec.y());
            out.writeVarInt(vec.z());
        }

        @Override
        public StorageBlockVec decode(MinecraftInputStream in) throws IOException {
            return new StorageBlockVec(
                    in.readVarInt(),
                    in.readVarInt(),
                    in.readVarInt()
            );
        }
    }
}
