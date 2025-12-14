package io.kloon.gameserver.modes.creative.tools;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.minestom.server.item.ItemStack;

public interface TickingTool {
    CreativeToolType getType();

    void tickHolding(CreativePlayer player, ItemStack item);

    default void tickWithoutHolding(CreativePlayer player) {}
}
