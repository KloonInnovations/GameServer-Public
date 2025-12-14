package io.kloon.gameserver.minestom.blocks.vanilla.interactions.impl;

import io.kloon.gameserver.minestom.blocks.family.InteractibleFamily;
import io.kloon.gameserver.minestom.blocks.handlers.TrapdoorBlock;
import io.kloon.gameserver.minestom.blocks.vanilla.interactions.VanillaBlockInteraction;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.sound.SoundEvent;

public class TrapdoorVanillaInteraction implements VanillaBlockInteraction {
    @Override
    public boolean handleInteract(Player player, Instance instance, Point blockPos, Point cursorPos, Block block) {
        Block inverted = TrapdoorBlock.OPEN.invertedOn(block);
        boolean open = TrapdoorBlock.OPEN.is(block);

        instance.setBlock(blockPos, inverted);

        InteractibleFamily family = InteractibleFamily.getByBlock(block);
        SoundEvent sound = open ? family.trapdoor().openSound() : family.trapdoor().closeSound();
        instance.playSoundExcept(player, Sound.sound(sound, Sound.Source.BLOCK, 1f, 1f), blockPos);

        return true;
    }
}
