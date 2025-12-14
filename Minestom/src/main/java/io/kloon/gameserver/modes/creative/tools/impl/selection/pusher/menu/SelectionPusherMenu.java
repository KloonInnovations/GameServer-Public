package io.kloon.gameserver.modes.creative.tools.impl.selection.pusher.menu;

import io.kloon.gameserver.modes.creative.tools.impl.selection.pusher.SelectionPusherTool;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.commands.ToolCommandsInfo;

public class SelectionPusherMenu extends CreativeToolMenu<SelectionPusherTool> {
    public SelectionPusherMenu(SelectionPusherTool tool, ItemRef itemRef) {
        super(tool, itemRef);
    }

    @Override
    protected void registerButtons() {
        reg(22, slot -> new SelectionPushDistanceButton(tool, itemRef, slot));

        reg().toolCommands(this);
    }
}
