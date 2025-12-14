package io.kloon.gameserver.modes.creative.tools.impl.erosion.menu;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.preferences.common.snipeshape.SnipeShapeButton;
import io.kloon.gameserver.modes.creative.menu.preferences.common.snipevisibility.SnipeVisibilityButton;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.ItemBoundNumberButton;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.NumberInput;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.ErosionTool;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.ErosionToolSettings;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.params.ErosionPreset;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.work.ErosionGen;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.SnipeSettingsMenu;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class ErosionToolMenu extends CreativeToolMenu<ErosionTool> {
    private final CreativePlayer player;

    public static final NumberInput<ErosionToolSettings> RADIUS = NumberInput.consumerInt(
            Material.NAUTILUS_SHELL, NamedTextColor.AQUA,
            "Erosion Radius",
            MM_WRAP."<gray>How many blocks around each side of the center.",
            ErosionGen.DEFAULT_RADIUS, 1, ErosionGen.MAX_RADIUS,
            ErosionToolSettings::getRadius, ErosionToolSettings::setRadius);

    public ErosionToolMenu(CreativePlayer player, ErosionTool tool, ItemRef itemRef) {
        super(tool, itemRef);
        this.player = player;
    }

    public ErosionToolSettings getSettings() {
        return tool.getItemBound(itemRef);
    }

    @Override
    protected void registerButtons() {
        ErosionPreset[] presets = ErosionPreset.values();
        int[] layout = { 10, 11, 12, 19, 20, 21 };
        for (int i = 0; i < Math.min(layout.length, presets.length); ++i) {
            int slot = layout[i];
            ErosionPreset preset = presets[i];
            reg(slot, new ErosionPresetButton(this, preset));
        }

        reg(15, slot -> new ErosionParamButton(slot, this,
                "Erosion Faces", Material.CHISELED_SANDSTONE, 6,
                new Lore().wrap("<gray>If at least this amount of adjacent blocks aren't solid blocks, the block will erode."),
                s -> (double) s.erosionFaces(),
                (s, value) -> s.withErosionFaces(value.intValue())));

        reg(16, slot -> new ErosionParamButton(slot, this,
        "Erosion Iterations", Material.SANDSTONE_WALL, 4,
                new Lore().wrap("<gray>How many times the erosion is applied each use."),
                s -> (double) s.erosionIterations(),
                (s, value) -> s.withErosionIterations(value.intValue())));

        reg(24, slot -> new ErosionParamButton(slot, this,
                "Fill Faces", Material.JUNGLE_LOG, 6,
                new Lore().wrap("<gray>If at least this amount of adjacent blocks are solid blocks, the block turns into the most common of those. Applies after erosion."),
                s -> (double) s.fillFaces(),
                (s, value) -> s.withFillFaces(value.intValue())));

        reg(25, slot -> new ErosionParamButton(slot, this,
                "Fill Iterations", Material.JUNGLE_FENCE, 4,
                new Lore().wrap("<gray>How many times the fill is applied each use."),
                s -> (double) s.fillIterations(),
                (s, value) -> s.withFillIterations(value.intValue())));

        reg(37, slot -> new ItemBoundNumberButton<>(slot, this, RADIUS).withShortHand("Radius"));

        reg(39, slot -> new SnipeShapeButton(slot, tool, player));
        reg(41, SnipeVisibilityButton::new);

        reg(43, new SnipeSettingsMenu(this));

        reg().toolCommands(this);
    }
}
