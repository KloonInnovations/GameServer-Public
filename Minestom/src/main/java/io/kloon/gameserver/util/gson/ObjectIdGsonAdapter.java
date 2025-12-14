package io.kloon.gameserver.util.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdGsonAdapter extends TypeAdapter<ObjectId> {
    @Override
    public void write(JsonWriter out, ObjectId value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.beginObject();
        out.name("$oid").value(value.toHexString());
        out.endObject();
    }

    @Override
    public ObjectId read(JsonReader in) throws IOException {
        ObjectId objectId = null;

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            if ("$oid".equals(name)) {
                String hexString = in.nextString();
                objectId = new ObjectId(hexString);
            }
        }
        in.endObject();

        return objectId;
    }
}
