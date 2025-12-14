package io.kloon.gameserver.minestom.blocks.vanilla.tiles.impl;

import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.BlockHandler;
import org.jetbrains.annotations.NotNull;

public class EnderChestHandler implements BlockHandler {
    public static final Key ID = Key.key("ender_chest");

    @Override
    public @NotNull Key getKey() {
        return ID;
    }
}
