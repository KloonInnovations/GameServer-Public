package io.kloon.gameserver.modes.creative.storage.blockvolume;

import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.storage.blockvolume.impl.BlockArray;
import io.kloon.gameserver.modes.creative.storage.blockvolume.impl.BlockEmpty;
import io.kloon.gameserver.modes.creative.storage.blockvolume.impl.BlockMap;
import io.kloon.gameserver.modes.creative.storage.blockvolume.palette.DumbPalette;
import io.kloon.gameserver.modes.creative.storage.blockvolume.util.RelativePos;
import io.kloon.gameserver.modes.creative.storage.blockvolume.util.VolumeBlockEntity;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockVolumeBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(BlockVolumeBuilder.class);

    private final Set<Block> types = new HashSet<>();
    private final Object2ObjectOpenHashMap<BlockVec, Block> data = new Object2ObjectOpenHashMap<>();
    private final Map<BlockVec, VolumeBlockEntity> blockEntities = new HashMap<>();

    private Vec min = new Vec(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    private Vec max = new Vec(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

    public BlockVolumeBuilder set(Point pointInWorld, Instance instance) {
        Block block = instance.getBlock(pointInWorld);
        return set(pointInWorld.blockX(), pointInWorld.blockY(), pointInWorld.blockZ(), block);
    }

    public BlockVolumeBuilder set(int worldX, int worldY, int worldZ, Instance instance) {
        return set(worldX, worldY, worldZ, instance.getBlock(worldX, worldY, worldZ));
    }

    public BlockVolumeBuilder set(Point pointInWorld, Block block) {
        return set(pointInWorld.blockX(), pointInWorld.blockY(), pointInWorld.blockZ(), block);
    }

    public BlockVolumeBuilder set(int worldX, int worldY, int worldZ, Block block) {
        types.add(block);
        BlockVec blockVec = new BlockVec(worldX, worldY, worldZ);
        data.put(blockVec, block);

        min = min.min(blockVec);
        max = max.max(blockVec);

        BlockHandler handler = block.handler();
        if (handler != null) {
            VolumeBlockEntity blockEntity = VolumeBlockEntity.fromBlock(block, handler);
            blockEntities.put(blockVec, blockEntity);
        }

        return this;
    }

    @Nullable
    public Block get(Point blockPos) {
        return get(blockPos.blockX(), blockPos.blockY(), blockPos.blockZ());
    }

    @Nullable
    public Block get(int worldX, int worldY, int worldZ) {
        BlockVec blockVec = new BlockVec(worldX, worldY, worldZ);
        return data.get(blockVec);
    }

    public Block.Getter getter(Instance instance) {
        return new Block.Getter() {
            @Override
            public @UnknownNullability Block getBlock(int x, int y, int z, @NotNull Block.Getter.Condition condition) {
                Block block = get(x, y, z);
                return block == null
                        ? instance.getBlock(x, y, z)
                        : block;
            }
        };
    }

    public int countBlocks() {
        return data.size();
    }

    public boolean isEmpty() {
        return countBlocks() == 0;
    }

    public BoundingBox getCuboid() {
        return BoundingBox.fromPoints(min, max.add(1, 1, 1));
    }

    public BlockVolume build() {
        if (data.isEmpty()) {
            return new BlockEmpty();
        }

        BoundingBox boundingBox = BoundingBox.fromPoints(min, max.add(1, 1, 1));
        double width = boundingBox.width();
        double height = boundingBox.height();
        double depth = boundingBox.depth();

        DumbPalette palette = new DumbPalette(types);

        long volume = BoundingBoxUtils.volumeRounded(boundingBox);
        double coverage = (double) data.size() / volume;
        if (coverage <= 0.6) {
            BlockVec position = new BlockVec(min);
            Map<RelativePos, Block> rel = new HashMap<>();
            data.forEach((blockVec, block) -> {
                RelativePos relPos = RelativePos.to(position, blockVec);
                rel.put(relPos, block);
            });

            Map<RelativePos, VolumeBlockEntity> relEntities = new HashMap<>();
            blockEntities.forEach((blockVec, blockEntity) -> {
                RelativePos relPos = RelativePos.to(position, blockVec);
                relEntities.put(relPos, blockEntity);
            });

            BlockVec dimensions = new BlockVec(max.min(min));
            return new BlockMap(position, dimensions, palette, rel, relEntities);
        } else {
            BlockVec position = new BlockVec(min);
            BlockVec dimensions = new BlockVec(width, height, depth);

            Block[] dataYZX = new Block[(int) volume];
            AtomicInteger nonNull = new AtomicInteger();
            data.forEach((blockVec, block) -> {
                RelativePos relPos = RelativePos.to(position, blockVec);
                try {
                    int index = BlockArray.computeIndexYZX(dimensions, relPos.dx(), relPos.dy(), relPos.dz());
                    dataYZX[index] = block;
                    nonNull.incrementAndGet();
                } catch (Throwable t) {
                    throw new RuntimeException(STR."Error at \{relPos}, dim \{dimensions}", t);
                }
            });

            Map<RelativePos, VolumeBlockEntity> relEntities = new HashMap<>();
            blockEntities.forEach((blockVec, blockEntity) -> {
                RelativePos relPos = RelativePos.to(position, blockVec);
                relEntities.put(relPos, blockEntity);
            });

            return new BlockArray(position, dimensions, palette, dataYZX, nonNull.get(), relEntities);
        }
    }
}
