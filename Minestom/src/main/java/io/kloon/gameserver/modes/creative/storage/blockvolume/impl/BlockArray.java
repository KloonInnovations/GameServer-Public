package io.kloon.gameserver.modes.creative.storage.blockvolume.impl;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.storage.blockvolume.BlockVolume;
import io.kloon.gameserver.modes.creative.storage.blockvolume.iterator.BlockConsumer;
import io.kloon.gameserver.modes.creative.storage.blockvolume.iterator.VolumeIterator;
import io.kloon.gameserver.modes.creative.storage.blockvolume.legacy.BlockArrayCodecV1;
import io.kloon.gameserver.modes.creative.storage.blockvolume.palette.DumbPalette;
import io.kloon.gameserver.modes.creative.storage.blockvolume.util.RelativePos;
import io.kloon.gameserver.modes.creative.storage.blockvolume.util.VolumeBlockEntity;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class BlockArray implements BlockVolume {
    private final BlockVec position;
    private final BlockVec dimensions;
    private final DumbPalette palette;
    private final Block[] dataYZX;
    private final int nonNullBlocks;
    private final Map<RelativePos, VolumeBlockEntity> blockEntities;

    public BlockArray(BlockVec position, BlockVec dimensions, DumbPalette palette, Block[] dataYZX, int nonNullBlocks, Map<RelativePos, VolumeBlockEntity> blockEntities) {
        this.position = position;
        this.dimensions = dimensions;
        this.palette = palette;
        this.dataYZX = dataYZX;
        this.nonNullBlocks = nonNullBlocks;
        this.blockEntities = blockEntities;

        int volume = getVolume();
        if (volume != dataYZX.length) {
            throw new IllegalArgumentException(STR."Mismatch between volume \{volume} and data length \{dataYZX.length}");
        }
    }

    public BlockVec getPosition() {
        return position;
    }

    public BlockVec getDimensions() {
        return dimensions;
    }

    public DumbPalette getPalette() {
        return palette;
    }

    public int getVolume() {
        return BoundingBoxUtils.blocksVolume(dimensions);
    }

    public Block[] getDataYZX() {
        return dataYZX;
    }

    public Map<RelativePos, VolumeBlockEntity> getBlockEntities() {
        return Collections.unmodifiableMap(blockEntities);
    }

    @Override
    public @Nullable Block getBlock(int x, int y, int z) {
        int dx = x - position.blockX();
        int dy = y - position.blockY();
        int dz = z - position.blockZ();
        int index = computeIndexYZX(dimensions, dx, dy, dz);
        if (index < 0 || index >= dataYZX.length) {
            return null;
        }
        Block block = dataYZX[index];

        RelativePos relativePos = new RelativePos(dx, dy, dz);
        VolumeBlockEntity blockEntity = blockEntities.get(relativePos);
        if (blockEntity != null) {
            block = blockEntity.inject(block);
        }

        return block;
    }

    public static int computeIndexYZX(BlockVec dimensions, int x, int y, int z) {
        int width = dimensions.blockX();
        int depth = dimensions.blockZ();

        return (y * width * depth) + (z * width) + x;
    }

    public static RelativePos computeYZX(BlockVec dimensions, int index) {
        int width = dimensions.blockX();
        int depth = dimensions.blockZ();

        int y = index / (width * depth);
        int remainder = index % (width * depth);
        int z = remainder / width;
        int x = remainder % width;
        return new RelativePos(x, y, z);
    }

    @Override
    public VolumeIterator iterator() {
        return new ArrayIterator();
    }

    @Override
    public int count() {
        return nonNullBlocks;
    }

    @Override
    public BoundingBox toCuboid() {
        return new BoundingBox(dimensions.blockX(), dimensions.blockY(), dimensions.blockZ(), position);
    }

    public class ArrayIterator implements VolumeIterator {
        private int index;

        @Override
        public boolean hasNext() {
            return index < dataYZX.length;
        }

        @Override
        public void next(BlockConsumer consumer) {
            RelativePos rel = computeYZX(dimensions, index);
            int worldX = position.blockX() + rel.dx();
            int worldY = position.blockY() + rel.dy();
            int worldZ = position.blockZ() + rel.dz();
            Block block = dataYZX[index];

            VolumeBlockEntity blockEntity = blockEntities.get(rel);
            if (blockEntity != null) {
                block = blockEntity.inject(block);
            }

            if (block != null) {
                consumer.consume(worldX, worldY, worldZ, block);
            }
            ++index;
        }
    }

    public static final MinecraftCodec<BlockArray> CODEC_V1 = BlockArrayCodecV1.INSTANCE;
    public static final MinecraftCodec<BlockArray> CODEC_V2 = new Codec();
    public static final class Codec implements MinecraftCodec<BlockArray> {
        public void encode(BlockArray array, MinecraftOutputStream out) throws IOException {
            out.writeBlockPosition(array.position);

            BlockVec dimensions = array.dimensions;
            out.writeBlockPosition(dimensions);

            DumbPalette.CODEC.encode(array.palette, out);
            int actualNonNullBlocks = 0;
            for (Block block : array.dataYZX) {
                if (block == null) {
                    out.writeVarInt(-1);
                } else {
                    int id = array.palette.getIdForBlock(block);
                    if (id < 0) throw new IllegalStateException(STR."Malformed palette, missing block \{block}");
                    out.writeVarInt(id);
                    ++actualNonNullBlocks;
                }
            }
            out.writeVarInt(actualNonNullBlocks);

            Map<RelativePos, VolumeBlockEntity> blockEntities = array.getBlockEntities();
            out.write(blockEntities, VolumeBlockEntity.MAP_CODEC);
        }

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

            Map<RelativePos, VolumeBlockEntity> blockEntities = in.read(VolumeBlockEntity.MAP_CODEC);

            return new BlockArray(position, dimensions, palette, data, nonNullBlocks, blockEntities);
        }
    }
}
