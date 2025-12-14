package io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.menu;

import io.kloon.gameserver.modes.creative.menu.preferences.common.snipevisibility.SnipeVisibilityButton;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.ItemBoundNumberButton;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.NumberInput;
import io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.PyramidTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.PyramidToolSettings;
import io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.work.PyramidGenSettings;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.ToolPatternSelectionButton;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolToggle;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.SnipeSettingsMenu;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class PyramidToolMenu extends CreativeToolMenu<PyramidTool> {
    private final ToolPatternSelectionButton patternSelection;

    private static final int PATTERN_SELECTION_SLOT = 11;

    public static final ToolToggle<PyramidToolSettings> HOLLOW = new ToolToggle<>(
            Material.GLASS, "Hollow",
            MM_WRAP."<gray>Is the pyramid hollowed out (its interior is air).",
            PyramidToolSettings::isHollow, PyramidToolSettings::setHollow);

    public static final ToolToggle<PyramidToolSettings> UPSIDE_DOWN = new ToolToggle<>(
            Material.HOPPER, "Upside-down",
            MM_WRAP."<gray>Easily accomplish what Ancient Egypt couldn't, an upside-down pyramid.",
            PyramidToolSettings::isUpsideDown, PyramidToolSettings::setUpsideDown);

    public static final NumberInput<PyramidToolSettings> STEPS = NumberInput.consumerInt(
            Material.SANDSTONE_STAIRS, NamedTextColor.YELLOW,
            "Amount of Steps",
            MM_WRAP."<gray>How many steps the pyramid has.",
            PyramidGenSettings.DEFAULT_STEP, 1, PyramidGenSettings.MAX_STEPS,
            PyramidToolSettings::getSteps, PyramidToolSettings::setSteps);

    public static final NumberInput<PyramidToolSettings> STEPS_HEIGHT = NumberInput.consumerInt(
            Material.SANDSTONE, NamedTextColor.YELLOW,
            "Steps Height",
            MM_WRAP."<gray>The height of each step of the pyramid.",
            PyramidGenSettings.DEFAULT_STEP_HEIGHT, 1, PyramidGenSettings.MAX_STEP_HEIGHT,
            PyramidToolSettings::getStepHeight, PyramidToolSettings::setStepHeight);

    public static final NumberInput<PyramidToolSettings> STEPS_LENGTH = NumberInput.consumerInt(
            Material.SANDSTONE_SLAB, NamedTextColor.YELLOW,
            "Steps Length",
            MM_WRAP."<gray>The width and depth of each step of the pyramid.",
            PyramidGenSettings.DEFAULT_STEP_LENGTH, 1, PyramidGenSettings.MAX_STEP_LENGTH,
            PyramidToolSettings::getStepLength, PyramidToolSettings::setStepLength);

    public PyramidToolMenu(PyramidTool tool, ItemRef itemRef) {
        super(tool, itemRef);
        this.patternSelection = new ToolPatternSelectionButton(this, PATTERN_SELECTION_SLOT, tool, itemRef);
    }

    public PyramidToolSettings getItemBound() {
        return tool.getItemBound(itemRef);
    }

    @Override
    protected void registerButtons() {
        reg(PATTERN_SELECTION_SLOT, patternSelection);

        reg(13, slot -> new PyramidStepsButton(slot, this, STEPS));
        reg(15, slot -> new ItemBoundNumberButton<>(slot, this, STEPS_HEIGHT));
        reg(16, slot -> new ItemBoundNumberButton<>(slot, this, STEPS_LENGTH));

        regSettingToggle(28, HOLLOW);
        regSettingToggle(30, UPSIDE_DOWN);
        reg(32, SnipeVisibilityButton::new);
        reg(34, new SnipeSettingsMenu(this));

        reg().toolCommands(this);
    }

    @Override
    public void handleClickPlayerInventoryWhileOpen(InventoryPreClickEvent event) {
        patternSelection.clickPlayerInventory(event);
    }
}
