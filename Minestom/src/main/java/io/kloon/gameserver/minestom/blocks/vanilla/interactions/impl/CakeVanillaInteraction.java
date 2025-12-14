package io.kloon.gameserver.minestom.blocks.vanilla.interactions.impl;

import io.kloon.gameserver.minestom.InventoryExtras;
import io.kloon.gameserver.minestom.blocks.handlers.CakeBlock;
import io.kloon.gameserver.minestom.blocks.vanilla.interactions.VanillaBlockInteraction;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;

public class CakeVanillaInteraction implements VanillaBlockInteraction {
    @Override
    public boolean handleInteract(Player player, Instance instance, Point blockPos, Point cursorPos, Block block) {
        ItemStack inHand = player.getItemInMainHand();

        int bites = CakeBlock.BITES.get(block);

        if (block.defaultState() == Block.CAKE) {
            Block cake = CakeBlock.CANDLE_TO_CAKE.get(inHand.material());
            if (cake != null && bites == 0) {
                InventoryExtras.consumeItemInMainHand(player);
                instance.setBlock(blockPos, cake);
                return true;
            }
        }

//        Material candleMat = CakeBlock.CANDLE_TO_CAKE.inverse().get(block);
//        if (candleMat != null) {
//
//        }

        bites++;
        if (bites >= 7) {
            instance.setBlock(blockPos, Block.AIR);
            player.playSound(Sound.sound(SoundEvent.ENTITY_PLAYER_BURP, Sound.Source.PLAYER, 1f, 1f));
        } else {
            Block eaten = CakeBlock.BITES.get(bites).on(block);
            instance.setBlock(blockPos, eaten);
            player.playSound(Sound.sound(SoundEvent.ENTITY_GENERIC_EAT, Sound.Source.BLOCK, 1f, 1f), blockPos);
        }

        return true;
    }
}
