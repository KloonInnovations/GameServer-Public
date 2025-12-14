package io.kloon.gameserver.util.serialization;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import net.kyori.adventure.text.Component;
import net.minestom.server.sound.SoundEvent;
import org.bson.types.ObjectId;

public class KloonJackson {
    public static final SimpleModule MODULE = new SimpleModule();
    static {
        MODULE.addSerializer(CreativeToolType.JACKSON_SERIALIZER);
        MODULE.addDeserializer(CreativeToolType.class, CreativeToolType.JACKSON_DESERIALIZER);

        MODULE.addSerializer(JacksonObjectIdCodec.SERIALIZER);
        MODULE.addDeserializer(ObjectId.class, JacksonObjectIdCodec.DESERIALIZER);

        MODULE.addSerializer(ChangeType.JACKON_SERIALIZER);
        MODULE.addDeserializer(ChangeType.class, ChangeType.JACKSON_DESERIALIZER);

        MODULE.addSerializer(MinestomJackson.COMPONENT_SERIALIZER);
        MODULE.addDeserializer(Component.class, MinestomJackson.COMPONENT_DESERIALIZER);

        MODULE.addSerializer(MinestomJackson.SOUND_EVENT_SERIALIZER);
        MODULE.addDeserializer(SoundEvent.class, MinestomJackson.SOUND_EVENT_DESERIALIZER);
    }
}
