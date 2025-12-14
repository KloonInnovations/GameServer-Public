package io.kloon.gameserver.modes.creative.tools.impl.selection.regular;

import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.commands.ToolCommandsInfo;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolToggle;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.SnipeSettingsMenu;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.selection.regular.SelectionTool.Preferences;

public class SelectionToolMenu extends CreativeToolMenu<SelectionTool> {
    public static final ToolToggle<Preferences> AUTO_DESELECT = new ToolToggle<>(
            Material.STONECUTTER, "Auto-Deselect",
            MM_WRAP."<gray>Whether to auto-deselect when left-clicking while a complete selection (green) is active.",
            Preferences::isAutoDeselect, Preferences::setAutoDeselect);

    public static final ToolToggle<Preferences> USING_RESIZE_ANCHORS = new ToolToggle<>(
            CornerGrabPreview.MATERIAL, "Use Resize Anchors",
            MM_WRAP."<gray>Shows anchors at the corners of the selection which can be clicked to resize the selection.",
            Preferences::isUsingResizeAnchors, Preferences::setUsingResizeAnchors);

    public SelectionToolMenu(SelectionTool tool, ItemRef itemRef) {
        super(tool, itemRef);
    }

    @Override
    protected void registerButtons() {
        regPreferenceToggle(12, AUTO_DESELECT);
        regPreferenceToggle(14, USING_RESIZE_ANCHORS);

        reg(31, new SnipeSettingsMenu(this));
        reg().toolCommands(this);
    }
}
