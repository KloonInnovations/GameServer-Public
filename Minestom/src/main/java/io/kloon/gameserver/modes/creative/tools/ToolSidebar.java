package io.kloon.gameserver.modes.creative.tools;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.modes.creative.CreativePlayer;

public interface ToolSidebar<ItemBound, PlayerBound> {
    Lore generate(CreativePlayer player, ItemBound itemBound, PlayerBound playerBound);
}
