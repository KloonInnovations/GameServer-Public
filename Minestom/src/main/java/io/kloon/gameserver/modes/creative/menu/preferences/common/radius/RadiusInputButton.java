package io.kloon.gameserver.modes.creative.menu.preferences.common.radius;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.legacy.NumberInput;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.legacy.NumberInputButton;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class RadiusInputButton extends NumberInputButton {
    public RadiusInputButton(int slot, CreativeTool<? extends RadiusSettings, ?> tool, ItemRef itemRef, int def, int max) {
        super(slot, createInput(tool, itemRef, def, max));
    }

    @Override
    public Component getYourLine(CreativePlayer player, double playerValue) {
        return MM."<gray>Radius: \{formatValue(playerValue)}";
    }

    private static NumberInput createInput(CreativeTool<? extends RadiusSettings, ?> tool, ItemRef itemRef, int def, int max) {
        return new NumberInput(Material.NAUTILUS_SHELL, NamedTextColor.AQUA, null,
                "Radius",
                MM_WRAP."<gray>How many blocks to transform around each side of your <snipe_target><gray>.",
                null, ToolDataType.ITEM_BOUND,
                def, 1, max,
                p -> (double) tool.getItemBound(itemRef).getRadius(),
                (p, value) -> tool.editItemBound(p, itemRef, s -> s.setRadius(value.intValue())));
    }
}
