package io.kloon.gameserver.minestom.io.codecs;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import net.minestom.server.coordinate.Pos;

public final class MinestomCodecs {
    private MinestomCodecs() {}

    public static final MinecraftCodec<Pos> POS = new PosCodec();
}
