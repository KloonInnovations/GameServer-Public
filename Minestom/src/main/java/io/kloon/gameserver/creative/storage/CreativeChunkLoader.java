package io.kloon.gameserver.creative.storage;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.saves.WorldSave;
import io.kloon.gameserver.creative.storage.saves.WorldSaveRepo;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.storage.datainworld.CreativeWorldStorage;
import io.kloon.infra.KloonNetworkInfra;
import io.kloon.infra.util.cutenames.PetNames;
import net.hollowcube.polar.minestom.PolarChunkLoader;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.WorldBorder;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.world.DimensionType;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class CreativeChunkLoader extends PolarChunkLoader {
    private static final Logger LOG = LoggerFactory.getLogger(CreativeChunkLoader.class);

    private WorldDef worldDef;

    private WorldSave seedingSave;
    private final byte[] polarBytes;

    private WorldSave latestSave;

    private final CreativeWorldStorage worldStorage;

    private final UUID instanceId;
    private final WorldSaveRepo repo;

    private final AtomicInteger savesCounter = new AtomicInteger(0);
    private WorldSave.Reason saveReason = WorldSave.Reason.UNKNOWN;

    public CreativeChunkLoader(WorldDef worldDef, WorldSave seedingSave, byte[] polarBytes, CreativeWorldStorage storage, UUID instanceId, WorldSaveRepo repo) {
        this.worldDef = worldDef;
        this.seedingSave = seedingSave;
        this.polarBytes = polarBytes;
        this.worldStorage = storage;
        this.instanceId = instanceId;
        this.repo = repo;

        this.latestSave = seedingSave;
    }

    public WorldDef getWorldDef() {
        return worldDef;
    }

    public void setWorldDef(WorldDef worldDef) {
        this.worldDef = worldDef;
    }

    public boolean hasSeedSave() {
        return polarBytes != null;
    }

    @Nullable
    public WorldSave getSeedingSave() {
        return seedingSave;
    }

    @Nullable
    public WorldSave getLatestSave() {
        return latestSave;
    }

    public void setSaveReason(WorldSave.Reason reason) {
        this.saveReason = reason;
    }

    public CreativeWorldStorage getWorldStorage() {
        return worldStorage;
    }

    @Override
    public CompletableFuture<byte[]> loadWorld() {
        return CompletableFuture.completedFuture(polarBytes);
    }

    @Override
    public void saveInstance(@NotNull Instance instance) {
        List<Chunk> chunksToSave = instance.getChunks().stream()
                .filter(this::isInBounds)
                .toList();
        saveChunks(chunksToSave);
    }

    @Override
    public CompletableFuture<Void> saveWorld(byte[] polarBytes) {
        LOG.info(STR."Saving world \{worldDef}");

        byte[] customData;
        try {
            customData = CreativeWorldStorage.MSG_PACK.writeValueAsBytes(worldStorage);
        } catch (Throwable t) {
            return CompletableFuture.failedFuture(t);
        }

        ObjectId saveId = new ObjectId();

        ObjectId loadedFrom = seedingSave == null ? null : seedingSave._id();
        int indexInSession = savesCounter.getAndIncrement();

        KloonNetworkInfra infra = Kgs.getInfra();
        String allocName = infra.allocationName();
        String serverName = infra.serverName();

        WorldSave save = new WorldSave(saveId, worldDef._id(), System.currentTimeMillis(), 2, saveReason,
                loadedFrom, indexInSession, allocName, serverName, instanceId, null);
        this.latestSave = save;

        return repo.saveData(save, polarBytes, customData).thenRun(() -> {
            LOG.info(STR."Saved world \{worldDef}");
        });
    }

    public boolean isInBounds(Chunk chunk) {
        return isInBounds(chunk.toPosition());
    }

    public boolean isInBounds(Point point) {
        DimensionType dimension = MinecraftServer.getDimensionTypeRegistry().get(DimensionType.OVERWORLD);
        if (dimension == null) return false;
        if (point.y() < dimension.minY() || point.y() > dimension.maxY()) {
            return false;
        }

        Pos center = worldStorage.getWorldCenter().toPos();
        int centerChunkX = center.chunkX();
        int centerChunkZ = center.chunkZ();

        int chunkX = point.chunkX();
        int chunkZ = point.chunkZ();

        int dx = Math.abs(centerChunkX - chunkX);
        int dz = Math.abs(centerChunkZ - chunkZ);

        int chunkRadius = worldStorage.getWorldSize().getChunksRadius();

        return dx <= chunkRadius && dz <= chunkRadius;
    }

    public WorldBorder getBoundsAsBorder() {
        Pos center = worldStorage.getWorldCenter().toPos();
        int chunksDiameter = worldStorage.getWorldSize().getChunksDiameter();

        return new WorldBorder(
                chunksDiameter * 16,
                center.blockX() + 8,
                center.blockZ() + 8,
                0, 0);
    }
}
