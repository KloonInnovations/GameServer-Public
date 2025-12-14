package io.kloon.gameserver.util.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.bson.types.ObjectId;

import java.io.IOException;

public class JacksonObjectIdCodec {
    public static final StdSerializer<ObjectId> SERIALIZER = new StdSerializer<>(ObjectId.class) {
        @Override
        public void serialize(ObjectId value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            byte[] bytes = value.toByteArray();
            gen.writeBinary(bytes);
        }
    };

    public static final StdDeserializer<ObjectId> DESERIALIZER = new StdDeserializer<>(ObjectId.class) {
        @Override
        public ObjectId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            byte[] binary = p.getBinaryValue();
            return new ObjectId(binary);
        }
    };
}
