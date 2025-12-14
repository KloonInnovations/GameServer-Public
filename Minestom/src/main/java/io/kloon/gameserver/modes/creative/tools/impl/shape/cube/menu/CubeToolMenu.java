package io.kloon.gameserver.modes.creative.tools.impl.shape.cube.menu;

import io.kloon.gameserver.modes.creative.menu.preferences.common.snipevisibility.SnipeVisibilityButton;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.CubeTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.CubeToolSettings;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.ToolPatternSelectionButton;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolToggle;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.SnipeSettingsMenu;
import io.kloon.gameserver.util.coordinates.Axis;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class CubeToolMenu extends CreativeToolMenu<CubeTool> {
    private final ToolPatternSelectionButton patternSelection;

    private static final int PATTERN_SELECTON_SLOT = 11;

    public static final ToolToggle<CubeToolSettings> HOLLOW = new ToolToggle<>(
            Material.GLASS, "Hollow",
            MM_WRAP."<gray>Is the cuboid hollowed out (filled with air).",
            CubeToolSettings::isHollow, CubeToolSettings::setHollow);

    public CubeToolMenu(CubeTool tool, ItemRef itemRef) {
        super(tool, itemRef);
        this.patternSelection = new ToolPatternSelectionButton(this, PATTERN_SELECTON_SLOT, tool, itemRef);
    }

    public CubeToolSettings getItemBound() {
        return tool.getItemBound(itemRef);
    }

    @Override
    protected void registerButtons() {
        CubeToolSettings settings = getItemBound();

        reg(PATTERN_SELECTON_SLOT, patternSelection);

        if (settings.hasCuboidButtons()) {
            reg(12, new CubeDimensionButton(this, "Width", Axis.X));
            reg(13, new CubeDimensionButton(this, "Height", Axis.Y));
            reg(14, new CubeDimensionButton(this, "Depth", Axis.Z));
        } else {
            reg(13, new CubeSidesButton(this));
        }
        regSettingToggle(15, HOLLOW);

        reg(30, SnipeVisibilityButton::new);
        reg(32, new SnipeSettingsMenu(this));

        reg().toolCommands(this);
    }

    @Override
    public void handleClickPlayerInventoryWhileOpen(InventoryPreClickEvent event) {
        patternSelection.clickPlayerInventory(event);
    }
}
