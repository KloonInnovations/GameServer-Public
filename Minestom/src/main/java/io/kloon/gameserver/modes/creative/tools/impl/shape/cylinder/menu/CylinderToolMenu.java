package io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.menu;

import io.kloon.gameserver.modes.creative.menu.preferences.common.snipevisibility.SnipeVisibilityButton;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.ItemBoundNumberButton;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.NumberInput;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.CylinderTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.CylinderToolSettings;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.work.CylinderGenSettings;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.ToolPatternSelectionButton;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolToggle;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.SnipeSettingsMenu;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class CylinderToolMenu extends CreativeToolMenu<CylinderTool> {
    private final ToolPatternSelectionButton patternSelection;

    private static final int PATTERN_SELECTION_SLOT = 10;

    public static final NumberInput<CylinderToolSettings> RADIUS = NumberInput.consumer(
            Material.NAUTILUS_SHELL, CylinderTool.TEXT_COLOR,
            "Cylinder Radius",
            MM_WRAP."<gray>How many blocks surround the cylinder from its center.",
            CylinderGenSettings.DEFAULT_RADIUS, 1, CylinderGenSettings.MAX_RADIUS,
            CylinderToolSettings::getRadius, CylinderToolSettings::setRadius);

    public static final NumberInput<CylinderToolSettings> THICKNESS = NumberInput.consumer(
            Material.BARREL, NamedTextColor.LIGHT_PURPLE,
            "Cylinder Thickness",
            MM_WRAP."<gray>How many blocks between the non-curvy faces of the cylinder.",
            CylinderGenSettings.DEFAULT_THICKNESS, 1, CylinderGenSettings.MAX_THICKNESS,
            CylinderToolSettings::getThickness, CylinderToolSettings::setThickness);

    public static final ToolToggle<CylinderToolSettings> HOLLOW = new ToolToggle<>(
            Material.GLASS, "Hollow",
            MM_WRAP."<gray>Is the cylinder hollowed out (filled with air).",
            CylinderToolSettings::isHollow, CylinderToolSettings::setHollow);

    public static final ToolToggle<CylinderToolSettings> EVEN = new ToolToggle<>(
            Material.KELP, "Centered",
            MM_WRAP."<gray>Whether the cylinder circle is generated at the center of your target block or on its grid location.\n\n<dark_gray>Disabling this adds a half-block to the radius.",
            CylinderToolSettings::isEven, CylinderToolSettings::setEven);

    public static final ToolToggle<CylinderTool.Preferences> ANIMATE = new ToolToggle<>(
            Material.EMERALD_BLOCK, "Animate Preview",
            MM_WRAP."<gray>Animate the cylinder's preview, like a carousel.",
            CylinderTool.Preferences::isAnimatePreview, CylinderTool.Preferences::setAnimatePreview);

    public CylinderToolMenu(CylinderTool tool, ItemRef itemRef) {
        super(tool, itemRef);
        this.patternSelection = new ToolPatternSelectionButton(this, PATTERN_SELECTION_SLOT, tool, itemRef);
    }

    public CylinderToolSettings getItemBound() {
        return tool.getItemBound(itemRef);
    }

    @Override
    protected void registerButtons() {
        reg(PATTERN_SELECTION_SLOT, patternSelection);
        reg(12, slot -> new ItemBoundNumberButton<>(slot, this, RADIUS).withCloseOnSet().withShortHand("Radius"));
        reg(14, slot -> new ItemBoundNumberButton<>(slot, this, THICKNESS).withCloseOnSet().withShortHand("Thickness"));
        regSettingToggle(16, EVEN);

        regSettingToggle(28, HOLLOW);
        regPreferenceToggle(30, ANIMATE);
        reg(32, SnipeVisibilityButton::new);
        reg(34, new SnipeSettingsMenu(this));

        reg().toolCommands(this);
    }

    @Override
    public void handleClickPlayerInventoryWhileOpen(InventoryPreClickEvent event) {
        patternSelection.clickPlayerInventory(event);
    }
}
