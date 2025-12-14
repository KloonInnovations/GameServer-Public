package io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.menu;

import io.kloon.gameserver.modes.creative.menu.preferences.common.snipevisibility.SnipeVisibilityButton;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.ItemBoundNumberButton;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.NumberInput;
import io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.SphereTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.SphereToolSettings;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.ToolPatternSelectionButton;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolToggle;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.SnipeSettingsMenu;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class SphereToolMenu extends CreativeToolMenu<SphereTool> {
    private final ToolPatternSelectionButton patternSelection;

    private static final int PATTERN_SELECTION_SLOT = 10;

    public static final NumberInput<SphereToolSettings> SPHERE_RADIUS = NumberInput.consumer(
            Material.NAUTILUS_SHELL, NamedTextColor.AQUA,
            "Sphere Radius",
            MM_WRAP."<gray>How many blocks around each side of the center.",
            SphereTool.DEFAULT_RADIUS, 1, 90,
            SphereToolSettings::getRadius, (s, value) -> s.setRadius(value.intValue()));

    public static final ToolToggle<SphereToolSettings> HOLLOW = new ToolToggle<>(
            Material.GLASS, "Hollow",
            MM_WRAP."<gray>Is the sphere hollowed out (filled with air).",
            SphereToolSettings::isHollow, SphereToolSettings::setHollow);

    public static final ToolToggle<SphereToolSettings> CENTERED = new ToolToggle<>(
            Material.KELP, "Centered",
            MM_WRAP."<gray>Whether the sphere is generated at the center of your target block or on its grid location.\n\n<dark_gray>Disabling this adds a half-block to the radius.",
            SphereToolSettings::isCentered, SphereToolSettings::setCentered);

    public static final ToolToggle<SphereTool.Preferences> ANIMATE = new ToolToggle<>(
            Material.LAPIS_BLOCK, "Animate Preview",
            MM_WRAP."<gray>Animate the sphere's preview.",
            SphereTool.Preferences::isAnimatePreview, SphereTool.Preferences::setAnimatePreview);

    public SphereToolMenu(SphereTool tool, ItemRef itemRef) {
        super(tool, itemRef);
        this.patternSelection = new ToolPatternSelectionButton(this, PATTERN_SELECTION_SLOT, tool, itemRef);
    }

    @Override
    protected void registerButtons() {
        reg(PATTERN_SELECTION_SLOT, patternSelection);
        reg(12, slot -> new ItemBoundNumberButton<>(slot, this, SPHERE_RADIUS).withCloseOnSet());
        regSettingToggle(14, HOLLOW);
        regSettingToggle(16, CENTERED);

        regPreferenceToggle(29, ANIMATE);
        reg(31, SnipeVisibilityButton::new);
        reg(33, new SnipeSettingsMenu(this));

        reg().toolCommands(this);
    }

    @Override
    public void handleClickPlayerInventoryWhileOpen(InventoryPreClickEvent event) {
        patternSelection.clickPlayerInventory(event);
    }
}
