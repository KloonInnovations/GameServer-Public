package io.kloon.gameserver.modes.creative.storage.blockvolume.util;

import io.kloon.gameserver.minestom.io.Leb128Utils;
import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import net.minestom.server.coordinate.BlockVec;

import java.io.IOException;

public record RelativePos(int dx, int dy, int dz) {
    public static RelativePos to(BlockVec base, int worldX, int worldY, int worldZ) {
        int dx = worldX - base.blockX();
        int dy = worldY - base.blockY();
        int dz = worldZ - base.blockZ();
        return new RelativePos(dx, dy, dz);
    }

    public static RelativePos to(BlockVec base, BlockVec point) {
        return to(base, point.blockX(), point.blockY(), point.blockZ());
    }

    public static RelativePos to(BlockVec base, IntPos point) {
        return to(base, point.x(), point.y(), point.z());
    }

    public static final Codec CODEC = new Codec();
    public static final class Codec implements MinecraftCodec<RelativePos> {
        @Override
        public void encode(RelativePos obj, MinecraftOutputStream out) throws IOException {
            Leb128Utils.writeIntUnsigned(out, obj.dx);
            Leb128Utils.writeIntUnsigned(out, obj.dy);
            Leb128Utils.writeIntUnsigned(out, obj.dz);
        }

        @Override
        public RelativePos decode(MinecraftInputStream in) throws IOException {
            return new RelativePos(
                    Leb128Utils.readIntUnsigned(in),
                    Leb128Utils.readIntUnsigned(in),
                    Leb128Utils.readIntUnsigned(in));
        }
    }
}
