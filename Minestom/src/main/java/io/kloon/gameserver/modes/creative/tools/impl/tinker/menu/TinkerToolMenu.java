package io.kloon.gameserver.modes.creative.tools.impl.tinker.menu;

import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkerTool;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.commands.ToolCommandsInfo;

public class TinkerToolMenu extends CreativeToolMenu<TinkerTool> {
    public TinkerToolMenu(TinkerTool tool, ItemRef itemRef) {
        super(tool, itemRef);
        setBreadcrumbs(CreativeToolType.TINKER.getMaterial(), "Tinker Menu", "Creating a block...");
    }

    @Override
    protected void registerButtons() {
        reg(size.middleCenter(), new TinkerBlockFromNowhere(this));

        reg(size.last(), new ToolCommandsInfo(tool));
    }
}
