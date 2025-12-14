package io.kloon.gameserver.minestom.blocks.vanilla.interactions.impl;

import io.kloon.gameserver.minestom.blocks.family.InteractibleFamily;
import io.kloon.gameserver.minestom.blocks.handlers.DoorBlock;
import io.kloon.gameserver.minestom.blocks.vanilla.interactions.VanillaBlockInteraction;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.sound.SoundEvent;

public class DoorVanillaInteraction implements VanillaBlockInteraction {
    @Override
    public boolean handleInteract(Player player, Instance instance, Point blockPos, Point cursorPos, Block block) {
        Block inverted = DoorBlock.OPEN.invertedOn(block);
        boolean open = DoorBlock.OPEN.is(inverted);
        DoorBlock.Half half = DoorBlock.HALF.get(block);

        instance.setBlock(blockPos, inverted);

        InteractibleFamily family = InteractibleFamily.getByBlock(block);
        SoundEvent sound = open ? family.door().openSound() : family.door().closeSound();
        instance.playSoundExcept(player, Sound.sound(sound, Sound.Source.BLOCK, 1f, 1f), blockPos);

        BlockFace relFace = half == DoorBlock.Half.UPPER ? BlockFace.BOTTOM : BlockFace.TOP;
        Point relPos = blockPos.relative(relFace);
        Block relBlock = instance.getBlock(relPos);

        if (relBlock.defaultState() != block.defaultState()) {
            return true;
        }

        instance.setBlock(relPos, DoorBlock.OPEN.get(open).on(relBlock));

        return true;
    }
}
