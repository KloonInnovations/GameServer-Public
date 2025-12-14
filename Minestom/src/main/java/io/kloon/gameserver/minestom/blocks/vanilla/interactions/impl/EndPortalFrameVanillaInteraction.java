package io.kloon.gameserver.minestom.blocks.vanilla.interactions.impl;

import io.kloon.gameserver.minestom.InventoryExtras;
import io.kloon.gameserver.minestom.blocks.handlers.EndPortalFrameBlock;
import io.kloon.gameserver.minestom.blocks.vanilla.interactions.VanillaBlockInteraction;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class EndPortalFrameVanillaInteraction implements VanillaBlockInteraction {
    @Override
    public boolean handleInteract(Player player, Instance instance, Point blockPos, Point cursorPos, Block block) {
        boolean hasEye = EndPortalFrameBlock.EYE.is(block);
        ItemStack inHand = player.getItemInMainHand();
        if (!hasEye && inHand.material() == Material.ENDER_EYE) {
            Block withEye = EndPortalFrameBlock.EYE.get(true).on(block);
            instance.setBlock(blockPos, withEye);
            InventoryExtras.consumeItemInMainHand(player);
            return true;
        }

        return false;
    }
}
