package io.kloon.gameserver.modes.creative.storage.datainworld.waypoints;

import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class WaypointsStorage {
    private final List<WaypointStorage> list = new ArrayList<>();

    public void add(WaypointStorage waypoint) {
        list.add(waypoint);
    }

    public void remove(WaypointStorage waypoint) {
        list.remove(waypoint);
    }

    @Nullable
    public WaypointStorage getByNameIgnoreCase(String name) {
        return list.stream().filter(wp -> wp.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Nullable
    public WaypointStorage getByUuid(UUID uuid) {
        return list.stream().filter(wp -> wp.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    @Nullable
    public WaypointStorage removeByUuid(UUID uuid) {
        WaypointStorage waypoint = getByUuid(uuid);
        if (waypoint == null) return null;
        remove(waypoint);
        return waypoint;
    }

    @Nullable
    public WaypointStorage getWorldSpawn() {
        return list.stream()
                .filter(WaypointStorage::isWorldSpawn)
                .findFirst().orElse(null);
    }

    public List<WaypointStorage> getList() {
        return Collections.unmodifiableList(list);
    }

    public int size() {
        return list.size();
    }
}