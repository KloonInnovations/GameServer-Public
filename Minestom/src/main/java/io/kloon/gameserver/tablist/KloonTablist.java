package io.kloon.gameserver.tablist;

import com.google.common.collect.Sets;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.minecraft.profiles.MinecraftProfile;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.MetadataDef;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import net.minestom.server.network.player.ClientSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public abstract class KloonTablist {
    private static final Logger LOG = LoggerFactory.getLogger(KloonTablist.class);

    protected final KloonPlayer viewer;

    private final Map<UUID, PlayerInfoUpdatePacket.Entry> localEntries = new HashMap<>();
    private final Map<UUID, CompletableFuture<PlayerInfoUpdatePacket.Entry>> remoteEntries = new HashMap<>();

    public KloonTablist(KloonPlayer viewer) {
        this.viewer = viewer;
    }

    public final Collection<PlayerInfoUpdatePacket.Entry> getEntriesAsLoaded() {
        Map<UUID, PlayerInfoUpdatePacket.Entry> map = new HashMap<>();
        remoteEntries.forEach((uuid, future) -> {
            PlayerInfoUpdatePacket.Entry entry;
            if (!future.isDone()) {
                return;
            } else if (future.isCancelled() || future.isCompletedExceptionally()) {
                return;
            } else {
                entry = future.getNow(null);
            }
            if (entry == null) {
                LOG.warn("Null entry in tablist");
                return;
            }
            map.put(uuid, entry);
        });
        map.putAll(localEntries);
        return map.values();
    }

    public final PlayerInfoUpdatePacket.Entry getOnlineEntry(Player target) {
        try {
            return makeOnlineEntry(target);
        } catch (Throwable t) {
            LOG.error("Error making online tablist entry", t);
            return createPlayerEntry(target, Component.text(target.getUsername()));
        }
    }

    protected abstract PlayerInfoUpdatePacket.Entry makeOnlineEntry(Player player);

    public final CompletableFuture<PlayerInfoUpdatePacket.Entry> getOfflineEntry(UUID uuid) {
        return makeOfflineEntry(uuid).handle((entry, t) -> {
            if (t != null) {
                LOG.error("Error making offline tablist entry", t);
                return createFakeEntry(uuid, MM."<red>Error!!");
            }
            return entry;
        });
    }

    protected abstract CompletableFuture<PlayerInfoUpdatePacket.Entry> makeOfflineEntry(UUID uuid);

    public void put(PlayerInfoUpdatePacket.Entry available) {
        localEntries.put(available.uuid(), available);
    }

    public void put(UUID uuid, CompletableFuture<PlayerInfoUpdatePacket.Entry> loading) {
        remoteEntries.put(uuid, loading);
    }

    public boolean has(UUID uuid) {
        return remoteEntries.containsKey(uuid) || localEntries.containsKey(uuid);
    }

    public void remove(UUID uuid) {
        localEntries.remove(uuid);
        remoteEntries.remove(uuid);
    }

    public void purgeUnknowns(Set<UUID> local, Set<UUID> remote) {
        {
            Set<UUID> unknownLocal = new HashSet<>(Sets.difference(localEntries.keySet(), local));
            unknownLocal.forEach(localEntries::remove);
        }
        {
            Set<UUID> unknownRemote = new HashSet<>(Sets.difference(remoteEntries.keySet(), remote));
            unknownRemote.forEach(remoteEntries::remove);
        }
    }

    protected final PlayerInfoUpdatePacket.Entry createFakeEntry(UUID uuid, Component displayName) {
        return new PlayerInfoUpdatePacket.Entry(
                uuid, "Noname",
                new ArrayList<>(), true, 1, GameMode.SURVIVAL,
                displayName,
                null, 0, false);
    }

    protected PlayerInfoUpdatePacket.Entry createPlayerEntry(Player player, Component displayName) {
        PlayerSkin skin = player.getSkin();
        List<PlayerInfoUpdatePacket.Property> prop = skin != null ? List.of(new PlayerInfoUpdatePacket.Property("textures", skin.textures(), skin.signature())) : List.of();

        ClientSettings settings = player.getSettings();
        byte hatIndex = ((MetadataDef.Entry.BitMask) MetadataDef.Player.IS_HAT_ENABLED).bitMask();

        return new PlayerInfoUpdatePacket.Entry(player.getUuid(), player.getUsername(),
                prop, true, player.getLatency(), player.getGameMode(),
                displayName,
                null, 0,
                (settings.displayedSkinParts() & hatIndex) == hatIndex);
    }

    protected final PlayerInfoUpdatePacket.Entry createProfileEntry(MinecraftProfile profile, Component displayName) {
        List<PlayerInfoUpdatePacket.Property> properties = profile.properties().stream()
                .map(prop -> new PlayerInfoUpdatePacket.Property(prop.name(), prop.value(), prop.signature()))
                .toList();

        return new PlayerInfoUpdatePacket.Entry(profile.uuid(), profile.username(),
                properties, true, 1, GameMode.SURVIVAL,
                displayName,
                null, 0,
                true);
    }

    public abstract Component getLoadingName(UUID uuid);
}
