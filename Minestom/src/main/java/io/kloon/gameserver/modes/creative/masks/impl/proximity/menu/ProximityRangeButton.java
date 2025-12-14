package io.kloon.gameserver.modes.creative.masks.impl.proximity.menu;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.impl.proximity.ProximityMask;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.legacy.NumberInput;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.legacy.NumberInputButton;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ProximityRangeButton extends NumberInputButton {
    private final ProximityMaskMenu menu;

    public ProximityRangeButton(int slot, ProximityMaskMenu menu) {
        super(slot, createInput(menu));
        this.menu = menu;
    }

    @Override
    public Component getYourLine(CreativePlayer player, double playerValue) {
        return MM."<gray>Range: <green>\{formatValue(playerValue)}";
    }

    private static NumberInput createInput(ProximityMaskMenu menu) {
        MaskWithData<ProximityMask.Data> mask = menu.getMask();

        Lore lore = new Lore();
        lore.wrap("<gray>What distance to check around each block.");
        lore.wrap("<dark_gray>This is a manhattan distance!");

        return new NumberInput(
                Material.NAUTILUS_SHELL, NamedTextColor.AQUA, null,
                "Range",
                lore.asList(),
                null, ToolDataType.MASK_BOUND,
                1, 1, 4,
                _ -> (double) mask.data().getRange(),
                (player, value) -> {
                    mask.data().setRange(value.intValue());
                    menu.updateMaskAndDisplay(player, mask);
                }
        );
    }
}
