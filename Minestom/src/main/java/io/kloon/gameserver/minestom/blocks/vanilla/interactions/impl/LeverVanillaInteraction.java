package io.kloon.gameserver.minestom.blocks.vanilla.interactions.impl;

import io.kloon.gameserver.minestom.blocks.properties.PropertyLookups;
import io.kloon.gameserver.minestom.blocks.vanilla.interactions.VanillaBlockInteraction;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.sound.SoundEvent;

public class LeverVanillaInteraction implements VanillaBlockInteraction {
    @Override
    public boolean handleInteract(Player player, Instance instance, Point blockPos, Point cursorPos, Block block) {
        Block inverted = PropertyLookups.POWERED.invertedOn(block);
        boolean powered = PropertyLookups.POWERED.is(inverted);
        instance.setBlock(blockPos, inverted);

        instance.playSound(Sound.sound(SoundEvent.BLOCK_LEVER_CLICK, Sound.Source.BLOCK, 0.3f, powered ? 1.1f : 1.0f));

        return true;
    }
}
