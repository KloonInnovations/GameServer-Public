package io.kloon.gameserver.modes.creative.storage.blockvolume.legacy;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.storage.blockvolume.impl.BlockArray;
import io.kloon.gameserver.modes.creative.storage.blockvolume.palette.DumbPalette;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.block.Block;

import java.io.IOException;
import java.util.HashMap;

public class BlockArrayCodecV1 implements MinecraftCodec<BlockArray> {
    public static final BlockArrayCodecV1 INSTANCE = new BlockArrayCodecV1();

    @Override
    public void encode(BlockArray array, MinecraftOutputStream out) throws IOException {
        out.writeBlockPosition(array.getPosition());

        BlockVec dimensions = array.getDimensions();
        out.writeBlockPosition(dimensions);

        DumbPalette palette = array.getPalette();
        DumbPalette.CODEC.encode(palette, out);

        int actualNonNullBlocks = 0;
        for (Block block : array.getDataYZX()) {
            if (block == null) {
                out.writeVarInt(-1);
            } else {
                int id = palette.getIdForBlock(block);
                if (id < 0) throw new IllegalStateException(STR."Malformed palette, missing block \{block}");
                out.writeVarInt(id);
                ++actualNonNullBlocks;
            }
        }
        out.writeVarInt(actualNonNullBlocks);
    }

    @Override
    public BlockArray decode(MinecraftInputStream in) throws IOException {
        BlockVec position = in.readBlockPosition();
        BlockVec dimensions = in.readBlockPosition();

        DumbPalette palette = DumbPalette.CODEC.decode(in);
        int volume = BoundingBoxUtils.blocksVolume(dimensions);
        Block[] data = new Block[volume];
        for (int i = 0; i < volume; ++i) {
            int id = in.readVarInt();
            if (id >= 0) {
                data[i] = palette.getBlockWithId(id);
            }
        }
        int nonNullBlocks = in.readVarInt();
        return new BlockArray(position, dimensions, palette, data, nonNullBlocks, new HashMap<>());
    }
}
