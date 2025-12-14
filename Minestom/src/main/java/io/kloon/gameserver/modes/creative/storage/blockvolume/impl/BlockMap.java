package io.kloon.gameserver.modes.creative.storage.blockvolume.impl;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolume;
import io.kloon.gameserver.modes.creative.storage.blockvolume.iterator.BlockConsumer;
import io.kloon.gameserver.modes.creative.storage.blockvolume.iterator.VolumeIterator;
import io.kloon.gameserver.modes.creative.storage.blockvolume.legacy.BlockMapCodecV1;
import io.kloon.gameserver.modes.creative.storage.blockvolume.palette.DumbPalette;
import io.kloon.gameserver.modes.creative.storage.blockvolume.util.RelativePos;
import io.kloon.gameserver.modes.creative.storage.blockvolume.util.VolumeBlockEntity;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BlockMap implements BlockVolume {
    private final BlockVec position;
    private final BlockVec dimensions;
    private final DumbPalette palette;
    private final Map<RelativePos, Block> map;
    private final Map<RelativePos, VolumeBlockEntity> blockEntities;

    public BlockMap(BlockVec position, BlockVec dimensions, DumbPalette palette, Map<RelativePos, Block> map, Map<RelativePos, VolumeBlockEntity> blockEntities) {
        this.position = position;
        this.dimensions = dimensions;
        this.palette = palette;
        this.map = map;
        this.blockEntities = blockEntities;
    }

    public static final MinecraftCodec<BlockMap> CODEC_V1 = BlockMapCodecV1.INSTANCE;
    public static final Codec CODEC_V2 = new Codec();
    public static class Codec implements MinecraftCodec<BlockMap> {
        @Override
        public void encode(BlockMap obj, MinecraftOutputStream out) throws IOException {
            out.writeBlockPosition(obj.position);

            DumbPalette palette = obj.palette;
            out.write(palette, DumbPalette.CODEC);

            Map<RelativePos, Block> map = obj.map;
            out.writeVarInt(map.size());
            for (Map.Entry<RelativePos, Block> entry : map.entrySet()) {
                RelativePos relPos = entry.getKey();
                Block block = entry.getValue();

                int id = palette.getIdForBlock(block);
                if (id < 0) throw new IllegalStateException(STR."Malformed palette, missing block \{block}");

                out.write(relPos, RelativePos.CODEC);
                out.writeVarInt(id);
            }

            Map<RelativePos, VolumeBlockEntity> blockEntities = obj.getBlockEntities();
            out.write(blockEntities, VolumeBlockEntity.MAP_CODEC);
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

            Map<RelativePos, VolumeBlockEntity> blockEntities = in.read(VolumeBlockEntity.MAP_CODEC);

            return new BlockMap(position, dimensions, palette, map, blockEntities);
        }
    }

    public BlockVec getPosition() {
        return position;
    }

    public DumbPalette getPalette() {
        return palette;
    }

    public Map<RelativePos, Block> getMap() {
        return Collections.unmodifiableMap(map);
    }

    public Map<RelativePos, VolumeBlockEntity> getBlockEntities() {
        return Collections.unmodifiableMap(blockEntities);
    }

    @Nullable
    public Block getRelBlock(int x, int y, int z) {
        RelativePos rel = new RelativePos(x, y, z);
        Block block = map.get(rel);

        VolumeBlockEntity blockEntity = blockEntities.get(rel);
        if (blockEntity != null) {
            block = blockEntity.inject(block);
        }

        return block;
    }

    @Override
    public @Nullable Block getBlock(int x, int y, int z) {
        int dx = x - position.blockX();
        int dy = y - position.blockY();
        int dz = z - position.blockZ();
        return getRelBlock(dx, dy, dz);
    }

    @Override
    public VolumeIterator iterator() {
        return new MapIterator();
    }

    @Override
    public int count() {
        return map.size();
    }

    @Override
    public BoundingBox toCuboid() {
        return new BoundingBox(dimensions.blockX(), dimensions.blockY(), dimensions.blockZ(), position);
    }

    public class MapIterator implements VolumeIterator {
        private final Iterator<Map.Entry<RelativePos, Block>> iterator = map.entrySet().iterator();

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public void next(BlockConsumer consumer) {
            Map.Entry<RelativePos, Block> entry = iterator.next();
            RelativePos rel = entry.getKey();
            int worldX = position.blockX() + rel.dx();
            int worldY = position.blockY() + rel.dy();
            int worldZ = position.blockZ() + rel.dz();
            Block block = entry.getValue();

            VolumeBlockEntity blockEntity = blockEntities.get(rel);
            if (blockEntity != null) {
                block = blockEntity.inject(block);
            }
            consumer.consume(worldX, worldY, worldZ, block);
        }
    }


}
