package io.kloon.gameserver.modes.creative.storage.datainworld;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.kloon.gameserver.modes.creative.history.ChangeType;
import io.kloon.gameserver.modes.creative.history.audit.AuditHistory;
import io.kloon.gameserver.modes.creative.storage.datainworld.minestom.StorageBlockVec;
import io.kloon.gameserver.modes.creative.storage.datainworld.minestom.StoragePos;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointsStorage;
import io.kloon.gameserver.modes.creative.storage.datainworld.world.CreativeTimeStorage;
import io.kloon.gameserver.modes.creative.storage.datainworld.world.CreativeWeatherStorage;
import io.kloon.gameserver.util.serialization.JacksonObjectIdCodec;
import io.kloon.gameserver.util.serialization.KloonJackson;
import org.bson.types.ObjectId;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.util.*;

public class CreativeWorldStorage {
    private final StorageBlockVec worldCenter = new StorageBlockVec(10_000, 41, 10_000);
    private final Map<UUID, PlayerWorldStorage> playerStorage = new HashMap<>();
    private final WaypointsStorage waypoints = new WaypointsStorage();
    private final AuditHistory auditHistory = new AuditHistory();
    private final CreativeWeatherStorage weather = new CreativeWeatherStorage();
    private final CreativeTimeStorage time = new CreativeTimeStorage();

    private CreativeWorldSize worldSize = CreativeWorldSize.SEVEN_X_SEVEN;
    private boolean canCommandJoin = true;

    public PlayerWorldStorage getPlayer(UUID playerId) {
        return playerStorage.computeIfAbsent(playerId, _ -> new PlayerWorldStorage());
    }

    public Set<UUID> getPlayerIds() {
        return Collections.unmodifiableSet(playerStorage.keySet());
    }

    public StorageBlockVec getWorldCenter() {
        return worldCenter;
    }

    public WaypointsStorage getWaypoints() {
        return waypoints;
    }

    public AuditHistory getAuditHistory() {
        return auditHistory;
    }

    public CreativeWeatherStorage getWeather() {
        return weather;
    }

    public CreativeTimeStorage getTime() {
        return time;
    }

    public CreativeWorldSize getWorldSize() {
        return worldSize;
    }

    public void setWorldSize(CreativeWorldSize worldSize) {
        this.worldSize = worldSize;
    }

    public boolean canCommandJoin() {
        return canCommandJoin;
    }

    public void setCanCommandJoin(boolean canCommandJoin) {
        this.canCommandJoin = canCommandJoin;
    }

    public static final ObjectMapper MSG_PACK = new ObjectMapper(new MessagePackFactory())
            .configure(MapperFeature.AUTO_DETECT_GETTERS, false)
            .configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false)
            .configure(MapperFeature.AUTO_DETECT_SETTERS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .registerModule(KloonJackson.MODULE)
            ;
}
