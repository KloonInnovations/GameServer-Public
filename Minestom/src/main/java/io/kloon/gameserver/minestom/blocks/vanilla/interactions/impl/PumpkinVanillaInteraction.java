package io.kloon.gameserver.minestom.blocks.vanilla.interactions.impl;

import io.kloon.gameserver.minestom.blocks.handlers.FacingXZBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.blocks.vanilla.interactions.VanillaBlockInteraction;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

public class PumpkinVanillaInteraction implements VanillaBlockInteraction {
    @Override
    public boolean handleInteract(Player player, Instance instance, Point blockPos, Point cursorPos, Block block) {
        if (player.getItemInMainHand().material() != Material.SHEARS) {
            return false;
        }
        FacingXZ facing = FacingXZ.fromPlayer(player).opposite();
        Block carved = FacingXZBlock.FACING_XZ.get(facing).on(Block.CARVED_PUMPKIN);
        instance.setBlock(blockPos, carved);

        player.playSound(Sound.sound(SoundEvent.BLOCK_PUMPKIN_CARVE, Sound.Source.BLOCK, 1f, 1f), blockPos);
        return true;
    }
}
