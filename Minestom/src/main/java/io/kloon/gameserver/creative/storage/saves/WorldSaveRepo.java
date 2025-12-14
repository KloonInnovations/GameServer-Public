package io.kloon.gameserver.creative.storage.saves;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.modes.creative.storage.datainworld.CreativeWorldStorage;
import io.kloon.infra.KloonNetworkInfra;
import io.kloon.infra.mongo.MongoRepo;
import io.kloon.infra.objectstorage.KloonBucket;
import io.kloon.infra.objectstorage.ObjectStorageBucket;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class WorldSaveRepo extends MongoRepo<WorldSave> {
    private final KloonNetworkInfra infra;
    private final ObjectStorageBucket worldsBucket;

    public WorldSaveRepo(KloonNetworkInfra infra) {
        super(infra.mongo(), "world_saves", WorldSave.class);
        this.infra = infra;

        KloonBucket kloonBucket = infra.datacenter().getCreativeWorldsBucket();
        this.worldsBucket = infra.getBucket(kloonBucket);
    }

    public WorldSaveRepo(KloonNetworkInfra infra, KloonBucket kloonBucket) {
        super(infra.mongo(), "world_saves", WorldSave.class);
        this.infra = infra;

        this.worldsBucket = infra.getBucket(kloonBucket);
    }

    @Override
    protected Stream<IndexModel> indexes() {
        return Stream.of(
                new IndexModel(Indexes.ascending("worldId")),
                new IndexModel(Indexes.ascending("timestamp"))
        );
    }

    public CompletableFuture<WorldSave> getWorldSave(ObjectId saveId) {
        return CompletableFuture.supplyAsync(() -> collection.find(Filters.eq("_id", saveId)).first());
    }

    public CompletableFuture<List<WorldSave>> getSaves(WorldDef worldDef) {
        return CompletableFuture.supplyAsync(() -> {
            return collection.find(Filters.eq("worldId", worldDef._id()), WorldSave.class)
                    .sort(Sorts.descending("timestamp"))
                    .limit(20).into(new ArrayList<>());
        });
    }

    public CompletableFuture<WorldSave> getLatestSave(WorldDef worldDef) {
        return getLatestSave(worldDef._id());
    }

    public CompletableFuture<WorldSave> getLatestSave(ObjectId worldId) {
        return CompletableFuture.supplyAsync(() -> {
            return collection.find(Filters.eq("worldId", worldId), WorldSave.class)
                    .sort(Sorts.descending("timestamp"))
                    .first();
        });
    }

    public CompletableFuture<WorldSaveWithData> getSaveData(WorldSave worldSave) {
        if (worldSave == null) {
            return CompletableFuture.completedFuture(null);
        }

        return worldsBucket.download(worldSave.hexId()).thenApply(bytes -> {
            if (worldSave.version() < 2) {
                return new WorldSaveWithData(worldSave, bytes, null);
            }

            try {
                StoredCreativeWorld data = CreativeWorldStorage.MSG_PACK.readValue(bytes, StoredCreativeWorld.class);
                return new WorldSaveWithData(worldSave, data.polarBytes(), data.customBytes());
            } catch (Throwable t) {
                throw new RuntimeException("Error deserializing world save", t);
            }
        });
    }

    public CompletableFuture<WorldSaveWithData> saveData(WorldSave save, byte[] polarBytes, byte[] customBytes) {
        return serializeWorld(polarBytes, customBytes)
                .thenCompose(bytes -> worldsBucket.upload(save.hexId(), bytes))
                .thenCompose(_ -> CompletableFuture.runAsync(() -> collection.insertOne(save)))
                .thenApply(_ -> new WorldSaveWithData(save, polarBytes, customBytes));
    }

    private CompletableFuture<byte[]> serializeWorld(byte[] polarBytes, byte[] customBytes) {
        return CompletableFuture.supplyAsync(() -> {
            StoredCreativeWorld stored = new StoredCreativeWorld(polarBytes, customBytes);
            try {
                return CreativeWorldStorage.MSG_PACK.writeValueAsBytes(stored);
            } catch (Throwable t) {
                throw new RuntimeException("Error serializing", t);
            }
        });
    }
}
