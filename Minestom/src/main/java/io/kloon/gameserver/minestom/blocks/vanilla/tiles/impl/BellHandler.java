package io.kloon.gameserver.minestom.blocks.vanilla.tiles.impl;

import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.BlockHandler;
import org.jetbrains.annotations.NotNull;

public class BellHandler implements BlockHandler {
    public static final Key ID = Key.key("bell");

    @Override
    public @NotNull Key getKey() {
        return ID;
    }
}
