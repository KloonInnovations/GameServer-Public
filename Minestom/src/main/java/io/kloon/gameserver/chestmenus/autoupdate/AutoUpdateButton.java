package io.kloon.gameserver.chestmenus.autoupdate;

import net.minestom.server.entity.Player;

public interface AutoUpdateButton {
    boolean shouldRerender(Player player);

    static void setup() {

    }
}
