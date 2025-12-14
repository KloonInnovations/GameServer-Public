package io.kloon.gameserver.tablist;

import com.google.common.collect.Sets;
import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket.*;

/*
It's a bit like a vdom.
It's doing Minestom's job, recording the packets sent to figure out the client's state
When the entries are set, it sends the right packets to stay consistent with the entries map
*/
public class VirtualTablist {
    private static final Logger LOG = LoggerFactory.getLogger(VirtualTablist.class);

    private final KloonPlayer player;

    private final Map<UUID, Entry> entries = new HashMap<>();

    private static final EnumSet<Action> ADD_PLAYER = EnumSet.of(Action.ADD_PLAYER, Action.UPDATE_LISTED, Action.UPDATE_DISPLAY_NAME);

    public VirtualTablist(KloonPlayer player) {
        this.player = player;
    }

    public void put(Entry entry, boolean sendPacket) {
        UUID uuid = entry.uuid();
        if (sendPacket) {
            Entry existing = entries.get(uuid);
            if (existing != null && equals(existing, entry)) {
                return;
            }

            remove(uuid, true);
            entries.put(uuid, entry);

            player.sendPacket(new PlayerInfoUpdatePacket(ADD_PLAYER, List.of(entry)));
        } else {
            entries.put(uuid, entry);
        }
    }

    public void remove(UUID entryUuid, boolean sendPacket) {
        Entry removed = entries.remove(entryUuid);
        if (removed == null) return;

        if (sendPacket) {
            player.sendPacket(new PlayerInfoRemovePacket(entryUuid));
        }
    }

    public void set(Collection<Entry> state, boolean sendPacket) {
        Set<UUID> stateIds = new HashSet<>(state.size());
        state.forEach(existing -> {
            stateIds.add(existing.uuid());
            put(existing, sendPacket);
        });

        Set<UUID> toRemove = Sets.difference(entries.keySet(), stateIds);
        new HashSet<>(toRemove).forEach(uuid -> remove(uuid, true));
    }

    private boolean equals(Entry a, Entry b) {
        return a.uuid().equals(b.uuid())
                && Objects.equals(a.displayName(), b.displayName());
    }
}
