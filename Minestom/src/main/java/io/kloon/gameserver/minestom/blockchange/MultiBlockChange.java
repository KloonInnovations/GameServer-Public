package io.kloon.gameserver.minestom.blockchange;

import io.kloon.gameserver.Kgs;
import net.minestom.server.coordinate.CoordConversion;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.PacketSendingUtils;
import net.minestom.server.utils.chunk.ChunkUtils;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultiBlockChange {
    private static final Logger LOG = LoggerFactory.getLogger(MultiBlockChange.class);

    private final Instance instance;
    private final Map<ChunkCoord, ChunkChange> changesByChunk = new HashMap<>();

    private boolean sparse = true;

    public MultiBlockChange(Instance instance) {
        this.instance = instance;
    }

    public MultiBlockChange contiguous() {
        this.sparse = false;
        return this;
    }

    public boolean sparse() {
        return sparse;
    }

    public DimensionType getDimension() {
        return instance.getCachedDimensionType();
    }

    public MultiBlockChange set(Point point, Block block) {
        return set(point.blockX(), point.blockY(), point.blockZ(), block);
    }

    public MultiBlockChange set(int x, int y, int z, Block block) {
        int chunkX = CoordConversion.globalToChunk(x);
        int chunkZ = CoordConversion.globalToChunk(z);
        ChunkCoord coord = new ChunkCoord(chunkX, chunkZ);
        ChunkChange chunkChange = changesByChunk.computeIfAbsent(coord, c -> new ChunkChange(this, c.chunkX, c.chunkZ));
        chunkChange.set(x, y, z, block);
        return this;
    }

    @Nullable
    private Chunk getAndLoadChunkFromServer(ChunkCoord coords) {
        Chunk chunk = instance.getChunk(coords.chunkX, coords.chunkZ);
        if (chunk == null) {
            chunk = instance.loadChunk(coords.chunkX, coords.chunkZ).join();
        }
        if (chunk == null || !chunk.isLoaded()) {
            LOG.warn(STR."Couldn't load chunk at \{coords} in MultiBlockChange");
            return null;
        }
        return chunk;
    }

    public MultiBlockChange applyToServer() {
        changesByChunk.forEach((coords, changes) -> {
            Chunk chunk = getAndLoadChunkFromServer(coords);
            if (chunk == null) return;
            changes.applyToServer(chunk);
        });
        return this;
    }

    public void broadcastToViewers() {
        changesByChunk.forEach((coords, changes) -> {
            Chunk chunk = getAndLoadChunkFromServer(coords);
            if (chunk == null) return;

            Set<Player> viewers = chunk.getViewers();
            changes.getPackets().forEach(packet -> {
                PacketSendingUtils.sendGroupedPacket(viewers, packet);
            });
        });
    }

    public void applyAndBroadcast() {
        applyToServer();
        broadcastToViewers();
    }

    private record ChunkCoord(int chunkX, int chunkZ) {}
}
