package io.kloon.gameserver.modes.creative.storage.playerdata;

import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.data.ToolData;
import io.kloon.infra.mongo.storage.BufferedDocument;
import org.bson.BsonDocumentReader;
import org.bson.Document;
import org.bson.codecs.JsonObjectCodec;
import org.bson.conversions.Bson;
import org.bson.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ToolsStorage {
    private static final Logger LOG = LoggerFactory.getLogger(ToolsStorage.class);
    
    private final BufferedDocument document;

    private final Map<CreativeToolType, Object> cache = new HashMap<>();

    public ToolsStorage(BufferedDocument document) {
        this.document = document;
    }

    public <T> T get(CreativeToolType type, Class<T> dataClass, Supplier<T> def) {
        Object cached = cache.computeIfAbsent(type, t -> {
            Bson bson = document.getBson(t.getDbKey());
            String json;
            if (bson instanceof Document doc) {
                JsonObjectCodec codec = new JsonObjectCodec();
                BsonDocumentReader reader = new BsonDocumentReader(doc.toBsonDocument());
                JsonObject jsonObject = codec.decode(reader, null);
                json = jsonObject.getJson();
            } else if (bson instanceof JsonObject jsonObj) {
                json = jsonObj.getJson();
            } else {
                return def.get();
            }

            return ToolData.DATA_GSON.fromJson(json, dataClass);
        });
        return (T) cached;
    }

    public void set(CreativeToolType type, Object data) {
        String json = ToolData.DATA_GSON.toJson(data);
        JsonObject bson = new JsonObject(json);
        document.putBson(type.getDbKey(), bson.toBsonDocument());
        cache.put(type, data);
    }
}
