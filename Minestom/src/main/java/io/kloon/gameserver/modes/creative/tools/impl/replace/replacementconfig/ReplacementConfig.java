package io.kloon.gameserver.modes.creative.tools.impl.replace.replacementconfig;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.storage.blockvolume.palette.DumbPalette;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ReplacementConfig {
    private final Map<Block, CreativePattern> mappingsExact;
    private final Map<Block, CreativePattern> mappingsAny;

    public static final int MAX_ENTRIES = 27;

    public ReplacementConfig() {
        this(new LinkedHashMap<>(), new LinkedHashMap<>());
    }

    public ReplacementConfig(Map<Block, CreativePattern> mappingsExact, Map<Block, CreativePattern> mappingsAny) {
        this.mappingsExact = mappingsExact;
        this.mappingsAny = mappingsAny;
    }

    @Nullable
    public CreativePattern get(Block block) {
        CreativePattern exactMatch = mappingsExact.get(block);
        if (exactMatch != null) {
            return exactMatch;
        }
        return mappingsAny.get(block.defaultState());
    }

    public void put(Block block, CreativePattern pattern, boolean exact) {
        if (exact) {
            mappingsExact.put(block, pattern);
        } else {
            mappingsAny.put(block.defaultState(), pattern);
        }
    }

    public void remove(Block block) {
        mappingsExact.remove(block);
        mappingsAny.remove(block.defaultState());
    }

    public void remove(Block block, boolean exact) {
        if (exact) {
            mappingsExact.remove(block);
        } else {
            mappingsAny.remove(block.defaultState());
        }
    }

    public int size() {
        return mappingsAny.size() + mappingsAny.size();
    }

    public List<ReplacementEntry> getAsList() {
        List<ReplacementEntry> list = new ArrayList<>(mappingsExact.size());
        mappingsExact.forEach((block, pattern) -> list.add(new ReplacementEntry(block, pattern, true)));
        mappingsAny.forEach((block, pattern) -> list.add(new ReplacementEntry(block, pattern, false)));
        return list;
    }

    public Lore lore() {
        Lore lore = new Lore();
        List<ReplacementEntry> entries = getAsList();
        int limit = 4;
        for (int i = 0; i < Math.min(entries.size(), limit); ++i) {
            ReplacementEntry entry = entries.get(i);
            lore.add(MM."\{entry.labelMM()}");
        }
        if (entries.size() > limit) {
            int over = entries.size() - limit;
            lore.add(MM."<dark_gray>And \{over} more...");
        }
        return lore;
    }

    public ReplacementConfig copy() {
        return new ReplacementConfig(copyMap(mappingsExact), copyMap(mappingsAny));
    }

    private static Map<Block, CreativePattern> copyMap(Map<Block, CreativePattern> map) {
        Map<Block, CreativePattern> copy = new HashMap<>(map.size());
        map.forEach((block, pattern) -> copy.put(block, pattern.copy()));
        return copy;
    }

    public static ReplacementConfig createDefault() {
        ReplacementConfig config = new ReplacementConfig();
        config.put(Block.STONE, new SingleBlockPattern(Block.RED_WOOL), false);
        return config;
    }

    public static final Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<ReplacementConfig> {
        @Override
        public void encode(ReplacementConfig obj, MinecraftOutputStream out) throws IOException {
            encodeMap(obj.mappingsExact, out);
            encodeMap(obj.mappingsAny, out);
        }

        private void encodeMap(Map<Block, CreativePattern> map, MinecraftOutputStream out) throws IOException {
            out.writeVarInt(map.size());
            for (Map.Entry<Block, CreativePattern> entry : map.entrySet()) {
                out.write(entry.getKey(), DumbPalette.BLOCK_CODEC_NO_TILE);
                out.write(entry.getValue(), CreativePattern.CODEC);
            }
        }

        @Override
        public ReplacementConfig decode(MinecraftInputStream in) throws IOException {
            return new ReplacementConfig(
                    decodeMap(in),
                    decodeMap(in)
            );
        }

        private Map<Block, CreativePattern> decodeMap(MinecraftInputStream in) throws IOException {
            int entries = in.readVarInt();
            Map<Block, CreativePattern> map = new LinkedHashMap<>(entries);
            for (int i = 0; i < entries; ++i) {
                Block block = in.read(DumbPalette.BLOCK_CODEC_NO_TILE);
                CreativePattern pattern = in.read(CreativePattern.CODEC);
                map.put(block, pattern);
            }
            return map;
        }
    }
}
