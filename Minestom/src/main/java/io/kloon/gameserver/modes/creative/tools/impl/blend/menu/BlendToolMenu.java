package io.kloon.gameserver.modes.creative.tools.impl.blend.menu;

import io.kloon.gameserver.chestmenus.listing.cycle.Cycle;
import io.kloon.gameserver.chestmenus.listing.cycle.CycleButton;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.preferences.common.radius.RadiusInputButton;
import io.kloon.gameserver.modes.creative.menu.preferences.common.snipeshape.SnipeShapeButton;
import io.kloon.gameserver.modes.creative.menu.preferences.common.snipevisibility.SnipeVisibilityButton;
import io.kloon.gameserver.modes.creative.tools.impl.blend.BlendSampling;
import io.kloon.gameserver.modes.creative.tools.impl.blend.BlendTool;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolSettingToggleButton;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolToggle;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.menu.SnipeSettingsMenu;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.blend.BlendTool.*;

public class BlendToolMenu extends CreativeToolMenu<BlendTool> {
    private final CreativePlayer player;

    private static final ToolToggle<Settings> SAMPLE_LIQUIDS = new ToolToggle<>(
            Material.WATER_BUCKET, "Sample Liquids",
            MM_WRAP."<gray>Whether liquids are sampled (recorded) when checking the nearby blocks.",
            Settings::isSampleLiquids, Settings::setSampleLiquids);

    private static final ToolToggle<Settings> CHANGE_ON_TIES = new ToolToggle<>(
            Material.RAW_COPPER, "Random on Ties",
            MM_WRAP."<gray>With this enabled, a tie in the highest frequency of nearby block types will pick a random one rather than stay unchanged.",
            Settings::isChangeOnTies, Settings::setChangeOnTies);

    public BlendToolMenu(BlendTool tool, ItemRef itemRef, CreativePlayer player) {
        super(tool, itemRef);
        this.player = player;
    }

    @Override
    protected void registerButtons() {
        reg(10, slot -> new RadiusInputButton(slot, tool, itemRef, Settings.DEFAULT_RADIUS, 21));

        Cycle<BlendSampling> sampling = new Cycle<>(BlendSampling.values());
        reg(12, slot -> new CycleButton<>(sampling, slot)
                .withIcon(Material.ACACIA_DOOR)
                .withTitle(MM."<title>Sampling")
                .withDescription(MM_WRAP."<gray>How many blocks to check around the <snipe_target><gray>.")
                .withOnClick((p, mode) -> {
                    CreativePlayer player = (CreativePlayer) p;
                    tool.editItemBound(player, itemRef, s -> s.setSampling(mode));
                    player.msg().send(MsgCat.TOOL,
                            NamedTextColor.LIGHT_PURPLE, "SAMPLING MODE!", MM."<gray>Switched to \{mode.label()}!",
                            SoundEvent.ENTITY_PARROT_IMITATE_ENDERMITE, Pitch.rng(1.85, 0.15), 0.7);
                }));

        reg(14, slot -> new ToolSettingToggleButton<>(slot, tool, itemRef, SAMPLE_LIQUIDS));
        reg(16, slot -> new ToolSettingToggleButton<>(slot, tool, itemRef, CHANGE_ON_TIES));

        reg(29, slot -> new SnipeShapeButton(slot, tool, player));
        reg(31, SnipeVisibilityButton::new);
        reg(33, new SnipeSettingsMenu(this));
    }
}
