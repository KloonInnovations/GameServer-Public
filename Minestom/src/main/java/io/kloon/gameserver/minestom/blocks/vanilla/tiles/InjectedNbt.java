package io.kloon.gameserver.minestom.blocks.vanilla.tiles;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.instance.block.Block;

public interface InjectedNbt {
    // modifies nbt whenever vanilla tiles are injected
    CompoundBinaryTag interjectNbt(Block block);
}
