package io.kloon.gameserver.modes.creative.storage.blockvolume.legacy;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.storage.blockvolume.impl.BlockMap;
import io.kloon.gameserver.modes.creative.storage.blockvolume.palette.DumbPalette;
import io.kloon.gameserver.modes.creative.storage.blockvolume.util.RelativePos;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.block.Block;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BlockMapCodecV1 implements MinecraftCodec<BlockMap> {
    public static final BlockMapCodecV1 INSTANCE = new BlockMapCodecV1();

    @Override
    public void encode(BlockMap obj, MinecraftOutputStream out) throws IOException {
        out.writeBlockPosition(obj.getPosition());

        DumbPalette palette = obj.getPalette();
        out.write(palette, DumbPalette.CODEC);

        Map<RelativePos, Block> map = obj.getMap();
        out.writeVarInt(map.size());
        for (Map.Entry<RelativePos, Block> entry : map.entrySet()) {
            RelativePos relPos = entry.getKey();
            Block block = entry.getValue();

            int id = palette.getIdForBlock(block);
            if (id < 0) throw new IllegalStateException(STR."Malformed palette, missing block \{block}");

            out.write(relPos, RelativePos.CODEC);
            out.writeVarInt(id);
        }
    }

    @Override
    public BlockMap decode(MinecraftInputStream in) throws IOException {
        BlockVec position = in.readBlockPosition();
        DumbPalette palette = in.read(DumbPalette.CODEC);

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        int entries = in.readVarInt();
        Map<RelativePos, Block> map = new HashMap<>(entries);
        for (int i = 0; i < entries; ++i) {
            RelativePos relPos = in.read(RelativePos.CODEC);
            int id = in.readVarInt();
            Block block = palette.getBlockWithId(id);
            if (block == null) {
                throw new IllegalStateException(STR."Malformed palette, missing block id \{id}");
            }

            minX = Math.min(relPos.dx(), minX);
            minY = Math.min(relPos.dx(), minY);
            minZ = Math.min(relPos.dx(), minZ);

            maxX = Math.max(relPos.dx(), maxX);
            maxY = Math.max(relPos.dx(), maxY);
            maxZ = Math.max(relPos.dx(), maxZ);

            map.put(relPos, block);
        }

        BlockVec dimensions = new BlockVec(maxX - minX, maxY - minY, maxZ - minZ);

        return new BlockMap(position, dimensions, palette, map, new HashMap<>());
    }
}
