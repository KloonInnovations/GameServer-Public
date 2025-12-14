package io.kloon.gameserver.modes.creative.jobs;

import net.minestom.server.entity.Player;

import java.util.UUID;

public record JobOwner(
        UUID uuid,
        String username
) {
    public boolean is(Player player) {
        return uuid.equals(player.getUuid());
    }

    public static JobOwner fromPlayer(Player player) {
        return new JobOwner(player.getUuid(), player.getUsername());
    }
}
