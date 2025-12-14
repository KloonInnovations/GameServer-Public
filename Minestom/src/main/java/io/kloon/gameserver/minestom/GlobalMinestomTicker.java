package io.kloon.gameserver.minestom;

import io.kloon.gameserver.minestom.events.EventHandler;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.server.ServerTickMonitorEvent;

import java.time.Instant;

public class GlobalMinestomTicker {
    private static final GlobalMinestomTicker INSTANCE = new GlobalMinestomTicker();

    private final Instant start = Instant.now();
    private int tick;

    private GlobalMinestomTicker() {}

    @EventHandler
    public void onTick(ServerTickMonitorEvent event) {
        ++tick;
    }

    public int tick() {
        return tick;
    }

    public static GlobalMinestomTicker getInstance() {
        return INSTANCE;
    }

    public static int getTick() {
        return INSTANCE.tick();
    }

    public static boolean every(int period, Entity entity) {
        int within = Math.abs(entity.getUuid().hashCode()) % period;
        return entity.getAliveTicks() % period == within;
    }
}
