package io.kloon.gameserver.modes.creative.tools.impl.selection.expander.menu;

import io.kloon.gameserver.modes.creative.tools.impl.selection.expander.SelectionExpanderTool;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.commands.ToolCommandsInfo;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolPreferenceToggleButton;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolToggle;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class SelectionExpanderMenu extends CreativeToolMenu<SelectionExpanderTool> {
    public static final ToolToggle<SelectionExpanderTool.Preferences> OPPOSITE_ON_SNEAK = new ToolToggle<>(
            Material.NETHERITE_LEGGINGS, "Opposite on Sneak",
            MM_WRAP."<gray>Use the opposite of the near face rather than using the far face when sneaking.",
            SelectionExpanderTool.Preferences::isOppositeOnSneak, SelectionExpanderTool.Preferences::setOppositeOnSneak);

    public SelectionExpanderMenu(SelectionExpanderTool tool, ItemRef itemRef) {
        super(tool, itemRef);
    }

    @Override
    protected void registerButtons() {
        reg(21, slot -> new SelectionExpanderDistanceButton(slot, this));
        reg(23, slot -> new ToolPreferenceToggleButton<>(slot, tool, OPPOSITE_ON_SNEAK));

        reg().toolCommands(this);
    }
}
