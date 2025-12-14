package io.kloon.gameserver.chestmenus.util;

import io.kloon.gameserver.util.cooldowns.impl.TickCooldown;
import io.kloon.gameserver.util.cooldowns.maps.PlayerTickCooldownMap;
import net.minestom.server.entity.Player;

public class ChestButtonCooldown {
    private static final PlayerTickCooldownMap GLOBAL_COOLDOWN = new PlayerTickCooldownMap(5);

    private final PlayerTickCooldownMap operationCooldown;

    public ChestButtonCooldown() {
        this(20);
    }

    public ChestButtonCooldown(int cooldownTicks) {
        this.operationCooldown = new PlayerTickCooldownMap(cooldownTicks);
    }

    // returns true if can continue
    public boolean check(Player player) {
        TickCooldown globalCd = GLOBAL_COOLDOWN.get(player);
        TickCooldown localCd = operationCooldown.get(player);
        if (globalCd.isOnCooldown() || localCd.isOnCooldown()) {
            return false;
        }
        globalCd.cooldown();
        localCd.cooldown();
        return true;
    }
}
