package io.kloon.gameserver.creative.storage.defs;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.creative.storage.deletion.WorldDeletion;
import io.kloon.gameserver.creative.storage.owner.WorldOwner;
import io.kloon.infra.KloonNetworkInfra;
import io.kloon.infra.mongo.MongoRepo;
import net.minestom.server.item.Material;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class WorldDefRepo extends MongoRepo<Document> {
    private static final Logger LOG = LoggerFactory.getLogger(WorldDefRepo.class);

    public WorldDefRepo(KloonNetworkInfra infra) {
        this(infra.mongo());
    }

    public WorldDefRepo(MongoDatabase mongo) {
        super(mongo, "world_defs", Document.class);
    }

    @Override
    protected Stream<IndexModel> indexes() {
        return Stream.of(
                new IndexModel(Indexes.ascending("ownership.playerId")),
                new IndexModel(Indexes.descending(WorldDef.CREATION_TIMESTAMP)),
                new IndexModel(Indexes.ascending("deletion.timestamp")),
                new IndexModel(Indexes.ascending("buildPermits.accountId")),
                new IndexModel(Indexes.ascending(WorldDef.PERMITS_IGNORED_BY))
        );
    }

    public CompletableFuture<List<WorldDef>> getWorldsByOwner(WorldOwner owner) {
        return CompletableFuture.supplyAsync(() -> {
            Bson filter = owner.getQueryFilter();
            ArrayList<Document> documents = collection.find(filter, Document.class).into(new ArrayList<>());
            return documents.stream().map(WorldDef::new).toList();
        });
    }

    public CompletableFuture<Long> countLiveWorldsByOwner(WorldOwner owner) {
        return CompletableFuture.supplyAsync(() -> {
            Bson filter = Filters.and(
                    owner.getQueryFilter(),
                    Filters.exists(WorldDef.DELETION, false)
            );
            return collection.countDocuments(filter);
        });
    }

    public CompletableFuture<List<WorldDef>> getWorldsForPermitOwner(ObjectId permitOwnerId) {
        return CompletableFuture.supplyAsync(() -> {
            Bson filter = Filters.and(
                    Filters.eq("buildPermits.accountId", permitOwnerId),
                    Filters.nin(WorldDef.PERMITS_IGNORED_BY, permitOwnerId)
            );
            ArrayList<Document> documents = collection.find(filter).limit(80).into(new ArrayList<>());
            return documents.stream().map(WorldDef::new).filter(def -> {
                BuildPermit permit = def.getPermitForPlayer(permitOwnerId);
                return permit != null && !permit.isExpired(null);
            }).toList();
        });
    }

    public CompletableFuture<WorldDef> getWorldDef(ObjectId worldId) {
        return CompletableFuture.supplyAsync(() -> {
            return collection.find(filter(worldId)).map(WorldDef::new).first();
        });
    }

    public CompletableFuture<Void> addIgnoresPermit(WorldDef world, ObjectId playerId) {
        return CompletableFuture.runAsync(() -> {
            Bson update = Updates.addToSet(WorldDef.PERMITS_IGNORED_BY, playerId);
            collection.updateOne(filter(world._id()), update);
        });
    }

    public CompletableFuture<Void> removeIgnoresPermit(WorldDef world, ObjectId playerId) {
        return CompletableFuture.runAsync(() -> {
            Bson update = Updates.pull(WorldDef.PERMITS_IGNORED_BY, playerId);
            collection.updateOne(filter(world._id()), update);
        });
    }

    public CompletableFuture<Void> insert(WorldDef def) {
        return CompletableFuture.runAsync(() -> def.getDocument().insertInto(collection));
    }

    public CompletableFuture<Void> update(WorldDef def) {
        return CompletableFuture.runAsync(() -> {
            Bson update = def.getDocument().collectUpdate();
            if (update == null) return;
            collection.updateOne(filter(def._id()), update);
        });
    }

    private Bson filter(ObjectId worldId) {
        return Filters.eq("_id", worldId);
    }
}
