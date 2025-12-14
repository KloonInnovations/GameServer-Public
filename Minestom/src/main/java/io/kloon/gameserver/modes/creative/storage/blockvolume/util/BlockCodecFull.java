package io.kloon.gameserver.modes.creative.storage.blockvolume.util;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BlockCodecFull implements MinecraftCodec<Block> {
    @Override
    public void encode(Block obj, MinecraftOutputStream out) throws IOException {
        out.writeString(obj.name());

        Map<String, String> properties = obj.properties();
        out.writeVarInt(properties.size());
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            out.writeString(entry.getKey());
            out.writeString(entry.getValue());
        }

        BlockHandler handler = obj.handler();
        out.writeBoolean(handler != null);
        if (handler != null) {
            out.writeString(handler.getKey().asString());
        }

        CompoundBinaryTag nbt = obj.nbt();
        out.writeBoolean(nbt != null);
        if (nbt != null) {
            out.writeNbt(nbt);
        }
    }

    @Override
    public Block decode(MinecraftInputStream in) throws IOException {
        String blockName = in.readString();
        Block block = Block.fromKey(blockName);
        if (block == null) throw new IllegalStateException(STR."Unknown block \{blockName}");

        int propertiesCount = in.readVarInt();
        Map<String, String> properties = new HashMap<>(propertiesCount);
        for (int i = 0; i < propertiesCount; ++i) {
            String key = in.readString();
            String value = in.readString();
            properties.put(key, value);
        }

        block = block.withProperties(properties);

        boolean hasHandler = in.readBoolean();
        if (hasHandler) {
            String handlerId = in.readString();
            BlockHandler handler = MinecraftServer.getBlockManager().getHandlerOrDummy(handlerId);
            block = block.withHandler(handler);
        }

        boolean hasNbt = in.readBoolean();
        if (hasNbt) {
            CompoundBinaryTag nbt = in.readNbt();
            block = block.withNbt(nbt);
        }

        return block;
    }
}
