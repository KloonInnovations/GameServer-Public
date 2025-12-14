package io.kloon.gameserver.modes.creative.storage.blockvolume.util;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import net.minestom.server.instance.block.Block;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BlockCodecNoTile implements MinecraftCodec<Block> {
    private static final String MINECRAFT_ONLY = "minecraft:";
    private static final LoadingCache<String, String> LONG_TO_SHORT = Caffeine.newBuilder().build(s -> s.replace(MINECRAFT_ONLY, ""));
    private static final LoadingCache<String, Block> SHORT_TO_BLOCK = Caffeine.newBuilder().build(s -> Block.fromKey(MINECRAFT_ONLY + s));

    @Override
    public void encode(Block obj, MinecraftOutputStream out) throws IOException {
        out.writeString(LONG_TO_SHORT.get(obj.name()));

        Map<String, String> properties = obj.properties();
        out.writeVarInt(properties.size());
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            out.writeString(entry.getKey());
            out.writeString(entry.getValue());
        }
    }

    @Override
    public Block decode(MinecraftInputStream in) throws IOException {
        String shortName = in.readString();
        Block block = SHORT_TO_BLOCK.get(shortName);
        if (block == null) throw new IllegalStateException(STR."Unknown block \{shortName}");

        int propertiesCount = in.readVarInt();
        Map<String, String> properties = new HashMap<>(propertiesCount);
        for (int i = 0; i < propertiesCount; ++i) {
            String key = in.readString();
            String value = in.readString();
            properties.put(key, value);
        }

        return block.withProperties(properties);
    }
}
