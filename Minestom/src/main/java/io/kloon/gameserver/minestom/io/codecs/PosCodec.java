package io.kloon.gameserver.minestom.io.codecs;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import net.minestom.server.coordinate.Pos;

import java.io.IOException;

public class PosCodec implements MinecraftCodec<Pos> {
    @Override
    public void encode(Pos obj, MinecraftOutputStream out) throws IOException {
        out.writeDouble(obj.x());
        out.writeDouble(obj.y());
        out.writeDouble(obj.z());
        out.writeFloat(obj.yaw());
        out.writeFloat(obj.pitch());
    }

    @Override
    public Pos decode(MinecraftInputStream in) throws IOException {
        return new Pos(
                in.readDouble(),
                in.readDouble(),
                in.readDouble(),
                in.readFloat(),
                in.readFloat());
    }
}
