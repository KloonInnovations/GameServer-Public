package io.kloon.gameserver.minestom;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.tablist.minestom.InstancePlayerList;
import io.kloon.infra.facts.KloonDataCenter;
import io.kloon.infra.util.Shorthand;
import net.kyori.adventure.key.Key;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.playerlist.PlayerList;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.stream.Stream;

public class KloonInstance extends InstanceContainer {
    public KloonInstance(@NotNull UUID uniqueId, @NotNull RegistryKey<DimensionType> dimensionType) {
        super(uniqueId, dimensionType);
    }

    public KloonInstance(@NotNull UUID uniqueId, @NotNull RegistryKey<DimensionType> dimensionType, @NotNull Key dimensionName) {
        super(uniqueId, dimensionType, dimensionName);
    }

    public KloonInstance(@NotNull UUID uniqueId, @NotNull RegistryKey<DimensionType> dimensionType, @Nullable IChunkLoader loader) {
        super(uniqueId, dimensionType, loader);
    }

    public KloonInstance(@NotNull UUID uniqueId, @NotNull RegistryKey<DimensionType> dimensionType, @Nullable IChunkLoader loader, @NotNull Key dimensionName) {
        super(uniqueId, dimensionType, loader, dimensionName);
    }

    @Override
    public PlayerList createPlayerList(Player viewer) {
        return new InstancePlayerList((KloonPlayer) viewer, this);
    }

    public Stream<? extends KloonPlayer> streamPlayers() {
        return getPlayers().stream().map(p -> (KloonPlayer) p);
    }

    public String getCuteName() {
        String inst = Shorthand.apply(uuid.toString(), 4);
        String alloc = Shorthand.apply(Kgs.getInfra().allocationName(), 4);
        KloonDataCenter datacenter = Kgs.getInfra().datacenter();
        return inst + "." + alloc + "#" + datacenter.getShortLabel();
    }
}
