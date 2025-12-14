package io.kloon.gameserver.minestom.itembuilder;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.infra.minecraft.profiles.MinecraftProfile;
import io.kloon.infra.mongo.accounts.projections.KloonMoniker;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.HeadProfile;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.network.player.PlayerSocketConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class SkinCache {
    private SkinCache() {}

    public static HeadProfile get(Player player) {
        PlayerConnection connection = player.getPlayerConnection();
        if (!(connection instanceof PlayerSocketConnection socketConn)) {
            return new HeadProfile(player.getUsername(), player.getUuid(), new ArrayList<>());
        }

        return toHead(socketConn.gameProfile());
    }

    public static CompletableFuture<HeadProfile> get(KloonMoniker moniker) {
        return Kgs.getCaches().profiles().getProfile(moniker.minecraftUuid()).thenApply(SkinCache::toHead);
    }

    public static CompletableFuture<HeadProfile> get(UUID minecraftUuid) {
        return Kgs.getCaches().profiles().getProfile(minecraftUuid).thenApply(SkinCache::toHead);
    }

    public static HeadProfile toHead(String skinValue) {
        return new HeadProfile(new PlayerSkin(skinValue, null));
    }

    public static HeadProfile toHead(MinecraftProfile profile) {
        List<HeadProfile.Property> properties = profile.properties().stream()
                .map(pp -> new HeadProfile.Property(pp.name(), pp.value(), pp.signature()))
                .toList();
        return new HeadProfile(
                profile.username(),
                profile.uuid(),
                properties
        );
    }

    public static HeadProfile toHead(GameProfile profile) {
        List<HeadProfile.Property> properties = profile.properties().stream()
                .map(pp -> new HeadProfile.Property(pp.name(), pp.value(), pp.signature()))
                .toList();
        return new HeadProfile(
                profile.name(),
                profile.uuid(),
                properties
        );
    }
}
