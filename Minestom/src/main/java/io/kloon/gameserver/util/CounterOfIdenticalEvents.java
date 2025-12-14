package io.kloon.gameserver.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.minestom.server.entity.Player;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CounterOfIdenticalEvents {
    private final Cache<UUID, Entry> entries;

    public CounterOfIdenticalEvents(long expiry, TimeUnit unit) {
        this.entries = Caffeine.newBuilder()
                .expireAfterWrite(expiry, unit)
                .build();
    }

    public int count(Player player, Object event) {
        return count(player.getUuid(), event);
    }

    public int count(UUID uuid, Object event) {
        Entry entry = entries.getIfPresent(uuid);
        if (entry == null || !Objects.equals(event, entry.event)) {
            entry = new Entry(event);
        }
        int count = entry.counter.incrementAndGet();
        entries.put(uuid, entry);
        return count;
    }

    private static class Entry {
        private final Object event;
        public AtomicInteger counter = new AtomicInteger();

        private Entry(Object event) {
            this.event = event;
        }
    }
}
