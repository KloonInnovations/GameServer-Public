package io.kloon.gameserver.util.mongo;

import io.kloon.infra.util.codecs.Decoder;
import io.kloon.infra.util.codecs.Encoder;
import org.bson.Document;
import org.bson.types.Binary;
import org.jetbrains.annotations.Nullable;

public abstract class MongoCodec<T> implements Encoder<T, Document>, Decoder<T, Document> {
    @Override
    public final Document encode(T t) {
        Document document = new Document();
        encodeInto(t, document);
        return document;
    }

    public abstract void encodeInto(T t, Document document);

    @Override
    public abstract T decode(Document document);

    @Nullable
    public static byte[] readBinary(Document document, String key) {
        Object obj = document.get(key);
        if (obj == null) {
            return null;
        }

        if (obj instanceof byte[]) {
            return (byte[]) obj;
        } else if (obj instanceof Binary bin) {
            return bin.getData();
        }
        throw new RuntimeException(STR."Unknown binary type: \{obj.getClass().getName()}");
    }
}
