package io.kloon.gameserver.util.cooldowns.maps;

import io.kloon.infra.util.cooldown.CooldownMap;
import io.kloon.infra.util.cooldown.impl.TimeCooldown;
import net.minestom.server.entity.Player;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerTimeCooldownMap extends CooldownMap<UUID, TimeCooldown> {
    public PlayerTimeCooldownMap(Duration duration) {
        super(() -> new TimeCooldown(duration));
    }

    public PlayerTimeCooldownMap(long amount, TimeUnit timeUnit) {
        super(() -> new TimeCooldown(amount, timeUnit));
    }

    public TimeCooldown get(Player player) {
        return get(player.getUuid());
    }
    public void forget(Player player) {
        forget(player.getUuid());;
    }
}
