package io.kloon.gameserver.modes.creative.vanilla;

import io.kloon.gameserver.minestom.events.EventHandler;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerPickBlockEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class BlockPickingListener {
    // from https://gist.github.com/mworzala/f478fb90fb65803648e3015b5ce3d88b found on Minestom discord
    @EventHandler
    public void onPickBlock(PlayerPickBlockEvent event) {
        if (!(event.getPlayer() instanceof CreativePlayer player)) return;
        if (player.getGameMode() != GameMode.CREATIVE) return; // Sanity
        if (! (player.getInstance() instanceof CreativeInstance instance)) return;

        // First try to get the block from the item registry
        Block block = event.getBlock();
        Material material = Material.fromKey(block.key());
        if (material == null) {
            player.sendPit(NamedTextColor.RED, "404!", MM."<gray>Couldn't pick an item for this block!");
            player.playSound(SoundEvent.BLOCK_BAMBOO_WOOD_PRESSURE_PLATE_CLICK_OFF, 0.5, 0.45);
            return;
        }
        ItemStack itemStack = ItemStack.of(material);

        PlayerInventory inventory = player.getInventory();

        // If the item is already on the hotbar, swap to it
        for (int i = 0; i < 9; i++) {
            if (!inventory.getItemStack(i).isSimilar(itemStack))
                continue;
            player.setHeldItemSlot((byte) i);
            break;
        }

        int targetSlot = player.getHeldSlot();
        ItemStack targetItem = inventory.getItemStack(targetSlot);
        if (targetItem.isSimilar(itemStack)) return;
        if (!targetItem.isAir()) {
            // Try to find an empty slot
            for (int i = 0; i < 9; i++) {
                if (inventory.getItemStack(i).isAir()) {
                    targetSlot = i;
                    break;
                }
            }
            // If we didnt find an empty slot its fine we can keep the original and replace.
        }

        // If the item already exists in the inventory, swap to it
        int existingSlot = -1;
        for (int i = 9; i < inventory.getSize(); i++) {
            if (inventory.getItemStack(i).isSimilar(itemStack)) {
                existingSlot = i;
                break;
            }
        }

        if (existingSlot != -1) {
            ItemStack existingItem = inventory.getItemStack(existingSlot);
            inventory.setItemStack(existingSlot, itemStack);
            inventory.setItemStack(targetSlot, existingItem);
        } else {
            inventory.setItemStack(targetSlot, itemStack);
            if (targetSlot != player.getHeldSlot()) {
                player.setHeldItemSlot((byte) targetSlot);
            }
        }
    }
}
