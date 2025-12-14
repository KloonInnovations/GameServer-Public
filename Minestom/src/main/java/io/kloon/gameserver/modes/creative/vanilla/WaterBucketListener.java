package io.kloon.gameserver.modes.creative.vanilla;

import io.kloon.gameserver.minestom.blocks.handlers.WaterBlock;
import io.kloon.gameserver.minestom.events.EventHandler;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public class WaterBucketListener {
    @EventHandler
    public void onUseWaterBucket(PlayerUseItemOnBlockEvent event) {
        if (!(event.getPlayer() instanceof CreativePlayer player)) {
            return;
        }
        if (!player.canEditWorld()) {
            return;
        }

        if (event.getHand() != PlayerHand.MAIN) return;

        ItemStack item = event.getItemStack();
        if (!item.material().equals(Material.WATER_BUCKET)) {
            return;
        }

        Instance instance = event.getInstance();
        Point blockPos = event.getPosition().relative(event.getBlockFace());

        Block block = instance.getBlock(blockPos);
        if (block.isAir()) {
            instance.setBlock(blockPos, Block.WATER);
        } else {
            block = WaterBlock.WATERLOGGED.invertedOn(block);
            instance.setBlock(blockPos, block);
        }
    }
}