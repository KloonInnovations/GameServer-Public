package io.kloon.gameserver.modes.creative.tools.menus;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolPreferenceToggleButton;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolSettingToggleButton;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolToggle;

public abstract class CreativeToolMenu<T extends CreativeTool> extends ChestMenu {
    protected final T tool;
    protected final ItemRef itemRef;

    public CreativeToolMenu(T tool, ItemRef itemRef) {
        this(tool, itemRef, tool.getType().getDisplayName());
    }

    public CreativeToolMenu(T tool, ItemRef itemRef, String name) {
        super(name);
        this.tool = tool;
        this.itemRef = itemRef;
    }

    public T getTool() {
        return tool;
    }

    public ItemRef getItemRef() {
        return itemRef;
    }

    public void regPreferenceToggle(int slot, ToolToggle toggle) {
        reg(slot, new ToolPreferenceToggleButton(slot, tool, toggle));
    }

    public void regSettingToggle(int slot, ToolToggle toggle) {
        reg(slot, new ToolSettingToggleButton<>(slot, tool, itemRef, toggle));
    }
}
