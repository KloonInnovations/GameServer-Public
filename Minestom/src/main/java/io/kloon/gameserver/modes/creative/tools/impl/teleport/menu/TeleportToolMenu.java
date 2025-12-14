package io.kloon.gameserver.modes.creative.tools.impl.teleport.menu;

import io.kloon.gameserver.modes.creative.tools.impl.teleport.TeleportTool;
import io.kloon.gameserver.modes.creative.tools.impl.teleport.menu.players.PlayersTeleportMenu;
import io.kloon.gameserver.modes.creative.tools.impl.teleport.menu.players.PlayersTeleportProxy;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;

public class TeleportToolMenu extends CreativeToolMenu<TeleportTool> {
    public TeleportToolMenu(TeleportTool tool, ItemRef itemRef) {
        super(tool, itemRef);
    }

    @Override
    protected void registerButtons() {
        reg(21, new TeleportRangeButton(tool, itemRef));
        reg(23, new PlayersTeleportProxy(this));

        reg().toolCommands(this);
    }
}
