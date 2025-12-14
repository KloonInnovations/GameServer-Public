package io.kloon.gameserver.minestom.blocks.vanilla.tiles;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class VanillaTiles {
    private final Map<Block, Supplier<BlockHandler>> tiles = new HashMap<>();

    public void register(Block block, Key tileId, Supplier<BlockHandler> handler) {
        tiles.put(block, handler);
        MinecraftServer.getBlockManager().registerHandler(tileId, handler);
    }

    @Nullable
    public BlockHandler createHandler(Block block) {
        Supplier<BlockHandler> supplier = tiles.get(block.defaultState());
        if (supplier == null) {
            return null;
        }
        return supplier.get();
    }

    public Block injectHandler(Block block) {
        BlockHandler handler = block.handler();
        if (handler != null) {
            if (handler instanceof InjectedNbt injectedNbt) {
                CompoundBinaryTag interjected = injectedNbt.interjectNbt(block);
                block = block.withNbt(interjected);
            }
            return block;
        }

        handler = createHandler(block);
        if (handler == null) {
            return block;
        }

        if (handler instanceof InjectedNbt injectedNbt) {
            CompoundBinaryTag interjected = injectedNbt.interjectNbt(block);
            block = block.withNbt(interjected);
        }

        return block.withHandler(handler);
    }
}
