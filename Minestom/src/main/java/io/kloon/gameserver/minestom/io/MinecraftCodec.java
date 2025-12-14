package io.kloon.gameserver.minestom.io;

import java.io.IOException;

public interface MinecraftCodec<T> extends MinecraftEncoder<T>, MinecraftDecoder<T> {
    @Override
    void encode(T obj, MinecraftOutputStream out) throws IOException;

    @Override
    T decode(MinecraftInputStream in) throws IOException;
}
