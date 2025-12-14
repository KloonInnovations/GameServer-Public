package io.kloon.gameserver.tablist;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.infra.cache.KloonCaches;
import io.kloon.infra.minecraft.profiles.MinecraftProfile;
import io.kloon.infra.mongo.accounts.projections.KloonMoniker;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class DefaultTablist extends KloonTablist {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultTablist.class);

    public static final String EARTH_AMERICA = "\uD83C\uDF0E"; // 🌎
    public static final String LOCAL_HEX = "#FF266E";

    public static final String EARTH_AFRICA = "\uD83C\uDF0D"; // 🌍
    public static final String REMOTE_HEX = "#9DFF26";

    public DefaultTablist(KloonPlayer viewer) {
        super(viewer);
    }

    @Override
    public Component getLoadingName(UUID uuid) {
        return MM."<gray>\uD83D\uDEDC Loading...";
    }

    @Override
    protected PlayerInfoUpdatePacket.Entry makeOnlineEntry(Player player) {
        boolean sameInstance = viewer.getInstance() == player.getInstance();
        String prefix = sameInstance ? STR."<\{LOCAL_HEX}>\{EARTH_AMERICA}" : STR."<\{REMOTE_HEX}>\{EARTH_AFRICA}";
        String usernameMM = player instanceof KloonPlayer kp ? kp.getColoredMM() : STR."<white>\{player.getUsername()}";
        Component displayName = MM."\{prefix} \{usernameMM}";
        return createPlayerEntry(player, displayName);
    }

    @Override
    public CompletableFuture<PlayerInfoUpdatePacket.Entry> makeOfflineEntry(UUID uuid) {
        KloonCaches caches = Kgs.getCaches();
        CompletableFuture<MinecraftProfile> getProfile = caches.profiles().getProfile(uuid);
        CompletableFuture<KloonMoniker> getMoniker = caches.monikers().getByMinecraftUuid(uuid);
        return CompletableFuture.allOf(getProfile, getMoniker).thenApplyAsync(_ -> {
            MinecraftProfile profile = getProfile.join();
            KloonMoniker moniker = getMoniker.join();

            String prefix = STR."<#9DFF26>\{EARTH_AFRICA}";
            String usernameMM = moniker.getColoredMM();
            Component displayName = MM."\{prefix} \{usernameMM}";

            return createProfileEntry(profile, displayName);
        }, MinecraftServer.getSchedulerManager());
    }
}
