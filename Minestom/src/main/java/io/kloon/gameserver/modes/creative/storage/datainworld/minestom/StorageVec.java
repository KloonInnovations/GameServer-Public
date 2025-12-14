package io.kloon.gameserver.modes.creative.storage.datainworld.minestom;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import net.minestom.server.coordinate.Vec;

import java.io.IOException;

public record StorageVec(double x, double y, double z) {
    public StorageVec(Vec vec) {
        this(vec.x(), vec.y(), vec.z());
    }

    public Vec toVec() {
        return new Vec(x, y, z);
    }

    public static final Codec CODEC = new Codec();
    public static final class Codec implements MinecraftCodec<StorageVec> {
        @Override
        public void encode(StorageVec obj, MinecraftOutputStream out) throws IOException {
            out.writeDouble(obj.x);
            out.writeDouble(obj.y);
            out.writeDouble(obj.z);
        }

        @Override
        public StorageVec decode(MinecraftInputStream in) throws IOException {
            return new StorageVec(in.readDouble(), in.readDouble(), in.readDouble());
        }
    }
}
