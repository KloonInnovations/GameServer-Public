package io.kloon.gameserver.util.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minestom.server.sound.SoundEvent;

import java.io.IOException;

public class MinestomJackson {
    public static final StdSerializer<Component> COMPONENT_SERIALIZER = new StdSerializer<>(Component.class) {
        @Override
        public void serialize(Component value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            String json = GsonComponentSerializer.gson().serialize(value);
            gen.writeString(json);
        }
    };

    public static final StdDeserializer<Component> COMPONENT_DESERIALIZER = new StdDeserializer<>(Component.class) {
        @Override
        public Component deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            String json = p.getValueAsString();
            return GsonComponentSerializer.gson().deserialize(json);
        }
    };

    public static final StdSerializer<SoundEvent> SOUND_EVENT_SERIALIZER = new StdSerializer<>(SoundEvent.class) {
        @Override
        public void serialize(SoundEvent value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(value.name());
        }
    };

    public static final StdDeserializer<SoundEvent> SOUND_EVENT_DESERIALIZER = new StdDeserializer<>(SoundEvent.class) {
        @Override
        public SoundEvent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            String soundName = p.getValueAsString();
            SoundEvent sound = SoundEvent.fromKey(soundName);
            if (sound == null) {
                sound = SoundEvent.BLOCK_NOTE_BLOCK_PLING;
            }
            return sound;
        }
    };
}
