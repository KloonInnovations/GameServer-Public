package io.kloon.gameserver.minestom.io;

import java.io.IOException;

public interface MinecraftEncoder<T> {
    void encode(T obj, MinecraftOutputStream out) throws IOException;
}
