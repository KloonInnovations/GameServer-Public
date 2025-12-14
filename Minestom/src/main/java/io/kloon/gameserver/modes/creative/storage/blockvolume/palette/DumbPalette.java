package io.kloon.gameserver.modes.creative.storage.blockvolume.palette;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.storage.blockvolume.util.BlockCodecFull;
import io.kloon.gameserver.modes.creative.storage.blockvolume.util.BlockCodecNoTile;
import net.minestom.server.instance.block.Block;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DumbPalette {
    private final Block[] blocks;
    private final Map<Block, Integer> indexes;

    public static final BlockCodecNoTile BLOCK_CODEC_NO_TILE = new BlockCodecNoTile();
    public static final BlockCodecFull BLOCK_CODEC_FULL = new BlockCodecFull();

    public DumbPalette(Set<Block> blocks) {
        this.blocks = blocks.toArray(Block[]::new);
        this.indexes = new HashMap<>();
        for (int i = 0; i < this.blocks.length; ++i) {
            indexes.put(this.blocks[i], i);
        }
    }

    public DumbPalette(Block[] blocks) {
        this.blocks = blocks;
        this.indexes = new HashMap<>();
        for (int i = 0; i < blocks.length; ++i) {
            indexes.putIfAbsent(blocks[i], i);
        }
    }

    public Block getBlockWithId(int id) {
        if (id < 0 || id >= blocks.length) throw new RuntimeException(STR."id \{id} outside of palette of \{blocks.length} blocks");
        return blocks[id];
    }

    // -1 if missing
    public int getIdForBlock(Block block) {
        return indexes.getOrDefault(block, -1);
    }

    public static Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<DumbPalette> {
        @Override
        public void encode(DumbPalette obj, MinecraftOutputStream out) throws IOException {
            Block[] blocks = obj.blocks;
            out.writeVarInt(blocks.length);
            for (Block block : blocks) {
                BLOCK_CODEC_NO_TILE.encode(block, out);
            }
        }

        @Override
        public DumbPalette decode(MinecraftInputStream in) throws IOException {
            int blocksCount = in.readVarInt();
            Block[] blocks = new Block[blocksCount];
            for (int i = 0; i < blocksCount; ++i) {
                blocks[i] = BLOCK_CODEC_NO_TILE.decode(in);
            }
            return new DumbPalette(blocks);
        }
    }
}
