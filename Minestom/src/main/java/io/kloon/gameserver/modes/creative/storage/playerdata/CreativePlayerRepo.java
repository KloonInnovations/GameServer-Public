package io.kloon.gameserver.modes.creative.storage.playerdata;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import io.kloon.infra.mongo.MongoRepo;
import io.kloon.infra.mongo.accounts.KloonAccount;
import io.kloon.infra.mongo.storage.BufferedDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class CreativePlayerRepo extends MongoRepo<Document> {
    private static final Logger LOG = LoggerFactory.getLogger(CreativePlayerRepo.class);

    public CreativePlayerRepo(MongoDatabase mongo) {
        super(mongo, "players_creative", Document.class);
    }

    @Override
    protected Stream<IndexModel> indexes() {
        return Stream.of(
                new IndexModel(Indexes.ascending(CreativePlayerStorage.ACCOUNT_ID), new IndexOptions().unique(true))
        );
    }

    public CompletableFuture<CreativePlayerStorage> get(KloonAccount account) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = collection.find(Filters.eq("accountId", account.getId())).first();
            if (document == null) {
                document = new Document();
            }
            BufferedDocument buffered = new BufferedDocument(document);
            return new CreativePlayerStorage(account, buffered);
        });
    }

    public CompletableFuture<Void> save(KloonAccount account, CreativePlayerStorage storage) {
        Bson update = storage.getDocument().collectUpdate();
        if (update == null) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.runAsync(() -> {
            collection.findOneAndUpdate(Filters.eq(CreativePlayerStorage.ACCOUNT_ID, account.getId()), update, new FindOneAndUpdateOptions().upsert(true));
        });
    }
}
