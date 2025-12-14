package io.kloon.gameserver.minestom.blockchange;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.minestom.blocks.KloonPlacementRules;
import it.unimi.dsi.fastutil.shorts.Short2IntMap;
import it.unimi.dsi.fastutil.shorts.Short2IntOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.play.BlockEntityDataPacket;
import net.minestom.server.network.packet.server.play.MultiBlockChangePacket;
import net.minestom.server.utils.block.BlockUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SectionChange {
    private static final Logger LOG = LoggerFactory.getLogger(SectionChange.class);

    private final Short2IntOpenHashMap stateIds = new Short2IntOpenHashMap(64, Short2IntOpenHashMap.FAST_LOAD_FACTOR);
    private final Short2ObjectOpenHashMap<BlockEntity> blockEntities = new Short2ObjectOpenHashMap<>(16);

    public void set(int sectionX, int sectionY, int sectionZ, Block block) {
        int index = sectionX << 8 | sectionZ << 4 | sectionY;
        short indexShort = (short) index;
        stateIds.put(indexShort, block.stateId());

        BlockEntity blockEntity = BlockEntity.fromBlock(block);
        if (blockEntity == null) {
            blockEntities.remove(indexShort);
        } else {
            blockEntities.put(indexShort, blockEntity);
        }
    }

    public void applyToServer(Chunk chunk, int section) {
        if (chunk instanceof LightingChunk lighting) {
            lighting.setFreezeInvalidation(true);
        }

        try {
            for (Short2IntMap.Entry e : stateIds.short2IntEntrySet()) {
                short position = e.getShortKey();
                int state = e.getIntValue();

                int sectionX = position >> 8;
                int sectionZ = (position >> 4) & 0xF;
                int sectionY = position & 0xF;

                int worldX = chunk.getChunkX() * 16 + sectionX;
                int worldY = section * 16 + sectionY;
                int worldZ = chunk.getChunkZ() * 16 + sectionZ;

                Block block = Block.fromStateId(state);

                BlockEntity blockEntity = blockEntities.get(position);
                if (blockEntity == null) {
                    block = KloonPlacementRules.injectHandler(block);
                } else {
                    block = blockEntity.inject(block);
                }

                chunk.setBlock(worldX, worldY, worldZ, block);
            }
        } finally {
            if (chunk instanceof LightingChunk lighting) {
                lighting.setFreezeInvalidation(false);

                lighting.invalidate();
                lighting.invalidateNeighborsSection(section);
                lighting.invalidateResendDelay();
            }
        }
    }

    public MultiBlockChangePacket getStateChangePacket(int chunkX, int section, int chunkZ) {
        long[] entries = new long[stateIds.size()];
        int index = 0;
        for (Short2IntMap.Entry e : stateIds.short2IntEntrySet()) {
            short position = e.getShortKey();
            long state = e.getIntValue();
            long entry = state << 12 | position;
            entries[index] = entry;
            ++index;
        }

        return new MultiBlockChangePacket(chunkX, section, chunkZ, entries);
    }

    public List<BlockEntityDataPacket> getBlockEntityPackets(int chunkX, int section, int chunkZ) {
        if (blockEntities.isEmpty()) {
            return Collections.emptyList();
        }

        List<BlockEntityDataPacket> packets = new ArrayList<>();
        for (Short2ObjectMap.Entry<BlockEntity> e : blockEntities.short2ObjectEntrySet()) {
            short position = e.getShortKey();
            BlockEntity blockEntity = e.getValue();
            int stateId = stateIds.get(position);
            if (stateId == stateIds.defaultReturnValue()) {
                LOG.warn("Missing id in block entities packet");
                continue;
            }

            int sectionX = position >> 8;
            int sectionZ = (position >> 4) & 0xF;
            int sectionY = position & 0xF;

            int worldX = chunkX * 16 + sectionX;
            int worldY = section * 16 + sectionY;
            int worldZ = chunkZ * 16 + sectionZ;

            Block block = Block.fromStateId(stateId);
            if (block == null || !block.registry().isBlockEntity()) {
                LOG.warn("Invalid block for block entities packet");
                continue;
            }

            block = blockEntity.inject(block);

            CompoundBinaryTag nbt = BlockUtils.extractClientNbt(block);
            packets.add(new BlockEntityDataPacket(new BlockVec(worldX, worldY, worldZ), block.registry().blockEntityId(), nbt));
        }

        return packets;
    }

    private record BlockEntity(@Nullable CompoundBinaryTag nbt, @Nullable BlockHandler handler) {
        public Block inject(Block block) {
            if (nbt != null) {
                block = block.withNbt(nbt);
            } if (handler != null) {
                block = block.withHandler(handler);
            }
            return block;
        }

        @Nullable
        public static SectionChange.BlockEntity fromBlock(Block block) {
            if (block.nbt() == null || block.handler() == null) {
                return null;
            }
            return new BlockEntity(block.nbt(), block.handler());
        }
    }
}
