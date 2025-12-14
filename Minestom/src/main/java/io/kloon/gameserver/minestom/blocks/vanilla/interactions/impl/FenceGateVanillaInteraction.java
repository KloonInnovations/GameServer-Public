package io.kloon.gameserver.minestom.blocks.vanilla.interactions.impl;

import io.kloon.gameserver.minestom.blocks.handlers.FenceGateBlock;
import io.kloon.gameserver.minestom.blocks.vanilla.interactions.VanillaBlockInteraction;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class FenceGateVanillaInteraction implements VanillaBlockInteraction {
    @Override
    public boolean handleInteract(Player player, Instance instance, Point blockPos, Point cursorPos, Block block) {
        Block inverted = FenceGateBlock.OPEN.invertedOn(block);
        instance.setBlock(blockPos, inverted);

        return true;
    }
}
