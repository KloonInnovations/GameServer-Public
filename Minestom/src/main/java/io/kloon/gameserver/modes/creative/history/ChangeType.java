package io.kloon.gameserver.modes.creative.history;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.modes.creative.history.builtin.*;
import io.kloon.gameserver.modes.creative.menu.worldadmin.time.TimeChange;
import io.kloon.gameserver.modes.creative.menu.worldadmin.weather.WeatherChange;
import io.kloon.gameserver.modes.creative.tools.impl.teleport.TeleportChange;
import io.kloon.gameserver.modes.creative.tools.impl.waypoint.changes.AddWaypointChange;
import io.kloon.gameserver.modes.creative.tools.impl.waypoint.changes.RemoveWaypointChange;
import io.kloon.infra.util.EnumQuery;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public enum ChangeType {
    UNKNOWN(0, UnknownChange.CODEC),
    SET_CUBOID(1, SetCuboidChange.CODEC),
    APPLY_VOLUME(2, ApplyVolumeChange.CODEC),
    TELEPORT(3, TeleportChange.CODEC),
    ADD_WAYPOINT(4, AddWaypointChange.CODEC),
    REMOVE_WAYPOINT(5, RemoveWaypointChange.CODEC),
    FULL_INVENTORY(6, FullInventoryChange.CODEC),
    ADJUST_SELECTION(7, SelectionChange.CODEC),
    BLOCK_CHANGE(8, BlockChange.CODEC),
    NO_CHANGE(9, NoChange.CODEC),
    ARMOR_INVENTORY(10, ArmorInventoryChange.CODEC),
    WEATHER(11, WeatherChange.CODEC),
    TIME(12, TimeChange.CODEC),
    MULTI(13, MultiChange.CODEC),
    ;

    private final int dbKey;
    private final MinecraftCodec<? extends Change> codec;

    ChangeType(int dbKey, MinecraftCodec<? extends Change> codec) {
        this.dbKey = dbKey;
        this.codec = codec;
    }

    public int getDbKey() {
        return dbKey;
    }

    public MinecraftCodec<? extends Change> getCodec() {
        return codec;
    }

    @Nullable
    public static ChangeType byDbKey(int ordinal) {
        return BY_DB_KEY.get(ordinal);
    }

    public static final ChangeType[] VALUES = values();
    private static final EnumQuery<Integer, ChangeType> BY_DB_KEY = new EnumQuery<>(VALUES, t -> t.dbKey);

    public static final StdSerializer<ChangeType> JACKON_SERIALIZER = new StdSerializer<>(ChangeType.class) {
        public void serialize(ChangeType value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeNumber(value.getDbKey());
        }
    };

    public static final StdDeserializer<ChangeType> JACKSON_DESERIALIZER = new StdDeserializer<>(ChangeType.class) {
        public ChangeType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            int ordinal = p.getNumberValue().intValue();
            ChangeType changeType = byDbKey(ordinal);
            return changeType == null ? ChangeType.UNKNOWN : changeType;
        }
    };
}
