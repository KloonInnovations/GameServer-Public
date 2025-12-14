package io.kloon.gameserver.modes.creative.storage.enderchest;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.Indexes;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.storage.inventories.items.CreativeItemDecoderV1;
import io.kloon.infra.mongo.MongoRepo;
import io.kloon.infra.mongo.storage.BufferedDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnderChestRepo extends MongoRepo<Document> {
    public EnderChestRepo(MongoDatabase mongo) {
        super(mongo, "creative_ender_chests", Document.class);
    }

    @Override
    protected Stream<IndexModel> indexes() {
        return Stream.of(
                new IndexModel(Indexes.ascending(EnderChestItem.PLAYER))
        );
    }

    public CompletableFuture<List<EnderChestItem>> getItems(CreativePlayer player) {
        ObjectId playerId = player.getAccountId();
        CreativeItemDecoderV1 decoder = new CreativeItemDecoderV1(player);
        return CompletableFuture.supplyAsync(() -> {
            ArrayList<Document> documents = collection.find(Filters.eq(EnderChestItem.PLAYER, playerId)).into(new ArrayList<>());
            return documents.stream()
                    .map(doc -> new EnderChestItem(new BufferedDocument(doc), decoder))
                    .collect(Collectors.toList());
        });
    }

    public CompletableFuture<Void> insert(EnderChestItem item) {
        return CompletableFuture.runAsync(() -> {
            item.getDocument().insertInto(collection);
        });
    }

    public CompletableFuture<Void> update(EnderChestItem item) {
        return CompletableFuture.runAsync(() -> {
            Bson update = item.getDocument().collectUpdate();
            if (update == null) {
                return;
            }
            collection.updateOne(Filters.eq(EnderChestItem.ID, item.getId()), update);
        });
    }

    public CompletableFuture<Void> delete(ObjectId itemId) {
        return CompletableFuture.runAsync(() -> {
            collection.deleteOne(Filters.eq(EnderChestItem.ID, itemId));
        });
    }
}
