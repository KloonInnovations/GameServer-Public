package io.kloon.gameserver.util.cooldowns.maps;

import io.kloon.gameserver.util.cooldowns.impl.TickCooldown;
import io.kloon.infra.util.cooldown.CooldownMap;
import net.minestom.server.entity.Player;

import java.util.UUID;

public class PlayerTickCooldownMap extends CooldownMap<UUID, TickCooldown> {
    public PlayerTickCooldownMap(long ticks) {
        super(() -> new TickCooldown(ticks));
    }

    public TickCooldown get(Player player) {
        return get(player.getUuid());
    }

    public void forget(Player player) {
        forget(player.getUuid());
    }
}
