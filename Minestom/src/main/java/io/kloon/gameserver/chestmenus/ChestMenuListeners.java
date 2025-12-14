package io.kloon.gameserver.chestmenus;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.signui.SignUxListener;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.network.packet.client.play.ClientUpdateSignPacket;

public class ChestMenuListeners {
    public static final SignUxListener SIGN_UX = new SignUxListener();

    public static void registerGlobal() {
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(InventoryPreClickEvent.class, event -> {
            Player player = event.getPlayer();
            AbstractInventory openInv = player.getOpenInventory();
            if (!(openInv instanceof ChestMenuInv chestMenuInv)) {
                return;
            }

            ChestMenu menu = chestMenuInv.getMenu();

            AbstractInventory clickedInv = event.getInventory();
            if (clickedInv == player.getInventory()) {
                event.setCancelled(true);
                menu.handleClickPlayerInventoryWhileOpen(event);
            } else if (clickedInv == openInv) {
                event.setCancelled(true);
                menu.handleClickMenuInventory(event);
            }
        });

        MinecraftServer.getPacketListenerManager().setPlayListener(ClientUpdateSignPacket.class, SIGN_UX);
    }
}
