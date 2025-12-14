package io.kloon.gameserver.modes.creative.menu;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.minestom.events.EventHandler;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.masks.MaskItem;
import io.kloon.gameserver.modes.creative.patterns.blocks.PatternBlock;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.ToolsListener;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerSwapItemEvent;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.client.play.ClientCreativeInventoryActionPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwapListener {
    private static final Logger LOG = LoggerFactory.getLogger(SwapListener.class);

    private final ToolsListener tools;

    public SwapListener(ToolsListener tools) {
        this.tools = tools;
    }

    @EventHandler
    public void onSwapOutsideMenu(PlayerSwapItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getOffHandItem(); // bruh

        event.setCancelled(true);
        ItemRef itemRef = ItemRef.mainHand(player);
        openItemMenu(player, item, itemRef, true);
    }

    @EventHandler(ignoreCancelled = false)
    public void onSwapInsideMenu(InventoryPreClickEvent event) {
        CreativePlayer player = (CreativePlayer) event.getPlayer();
        AbstractInventory openInv = player.getOpenInventory();
        if (!(openInv instanceof ChestMenuInv)) {
            return;
        }

        AbstractInventory clickedInv = event.getInventory();
        if (clickedInv != player.getInventory()) {
            return;
        }

        if (! (event.getClick() instanceof Click.OffhandSwap)) {
            return;
        }

        event.setCancelled(true);
        ItemRef itemRef = ItemRef.slot(player, event.getSlot());
        openItemMenu(player, event.getClickedItem(), itemRef, false);
    }

    private void openItemMenu(Player p, ItemStack item, ItemRef itemRef, boolean fallbackToMainMenu) {
        CreativePlayer player = (CreativePlayer) p;

        MaskItem maskItem = MaskItem.get(item);
        if (maskItem != null) {
            maskItem.openEditMenu(player, itemRef);
            return;
        }

        PatternBlock patternBlock = PatternBlock.get(item);
        if (patternBlock != null) {
            ChestMenu editMenu = patternBlock.createEditMenu(null, itemRef);
            if (editMenu != null) {
                editMenu.display(player);
                return;
            }
        }

        CreativeTool tool = tools.get(item);
        if (tool == null && fallbackToMainMenu) {
            tool = tools.get(CreativeToolType.MENU);
        }
        if (tool == null) {
            return;
        }

        tool.openSettingsMenu(player, itemRef);
    }

    @EventHandler
    public void openMenuOnSwap(InventoryPreClickEvent event) {
        CreativePlayer player = (CreativePlayer) event.getPlayer();
        AbstractInventory clickedInv = event.getInventory();
        if (!(clickedInv instanceof ChestMenuInv menuInv)) {
            return;
        }

        if (event.getClick() instanceof Click.HotbarSwap) {
            event.setCancelled(true);
            if (menuInv.getMenu() instanceof CreativeMainMenu) {
                player.closeInventory();
            } else {
                new CreativeMainMenu(player).display(player);
            }
        }
    }
}
