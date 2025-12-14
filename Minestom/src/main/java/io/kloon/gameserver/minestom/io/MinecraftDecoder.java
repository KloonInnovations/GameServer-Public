package io.kloon.gameserver.minestom.io;

import java.io.IOException;

public interface MinecraftDecoder<T> {
    T decode(MinecraftInputStream in) throws IOException;
}
