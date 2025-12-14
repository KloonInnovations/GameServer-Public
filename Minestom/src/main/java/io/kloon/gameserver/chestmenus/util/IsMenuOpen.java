package io.kloon.gameserver.chestmenus.util;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

import java.util.UUID;
import java.util.function.BooleanSupplier;

public class IsMenuOpen implements BooleanSupplier {
    private final UUID playerId;
    private final ChestMenu menu;

    public IsMenuOpen(Player player, ChestMenu menu) {
        this.playerId = player.getUuid();
        this.menu = menu;
    }

    @Override
    public boolean getAsBoolean() {
        Player player = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(playerId);
        if (player == null) return false;
        return player.getOpenInventory() instanceof ChestMenuInv chestMenuInv
                && chestMenuInv.getMenu() == menu;
    }
}
