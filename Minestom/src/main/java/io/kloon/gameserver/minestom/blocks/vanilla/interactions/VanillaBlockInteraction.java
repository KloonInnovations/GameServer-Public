package io.kloon.gameserver.minestom.blocks.vanilla.interactions;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public interface VanillaBlockInteraction {
    // returns true if handled and thus blocks further interaction, false if unhandled
    boolean handleInteract(Player player, Instance instance, Point blockPos, Point cursorPos, Block block);
}
