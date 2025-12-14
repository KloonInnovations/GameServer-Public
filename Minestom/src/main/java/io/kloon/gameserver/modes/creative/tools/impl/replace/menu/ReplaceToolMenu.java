package io.kloon.gameserver.modes.creative.tools.impl.replace.menu;

import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.modes.creative.tools.impl.replace.ReplaceTool;
import io.kloon.gameserver.modes.creative.tools.impl.replace.replacementconfig.ReplacementConfig;
import io.kloon.gameserver.modes.creative.tools.impl.replace.replacementconfig.ReplacementEntry;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.commands.ToolCommandsInfo;

import java.util.ArrayList;
import java.util.List;

public class ReplaceToolMenu extends CreativeToolMenu<ReplaceTool> {
    public ReplaceToolMenu(ReplaceTool tool, ItemRef itemRef) {
        super(tool, itemRef);
        tool.getItemBound(itemRef);
        setBreadcrumbs(tool.getType().getMaterial(), "Replace Tool", "Editing replacement config...");
    }

    public ReplacementConfig getReplacementConfig() {
        return tool.loadReplacementsSafe(itemRef);
    }

    @Override
    protected void registerButtons() {
        ReplacementConfig replacementConfig = getReplacementConfig();
        List<ReplacementEntry> entries = replacementConfig.getAsList();

        List<ChestButton> buttons = new ArrayList<>();
        entries.forEach(entry -> {
            buttons.add(new ReplacementEntryMenu(this, entry));
        });

        if (entries.size() < ReplacementConfig.MAX_ENTRIES) {
            buttons.add(new ReplacementEntryMenu(this, null));
        }

        ChestLayouts.INSIDE.distribute(buttons, this::reg);

        reg().toolCommands(this);
    }
}
