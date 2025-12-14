package io.kloon.gameserver.modes.creative.tools.impl.selection.expander.menu;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.legacy.NumberInput;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.legacy.NumberInputButton;
import io.kloon.gameserver.modes.creative.tools.impl.selection.expander.SelectionExpanderTool;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class SelectionExpanderDistanceButton extends NumberInputButton {
    public static final int MIN = 1;
    public static final int MAX = 256;

    public SelectionExpanderDistanceButton(int slot, SelectionExpanderMenu menu) {
        super(slot, createInput(menu));
    }

    @Override
    public Component getYourLine(CreativePlayer player, double blocks) {
        return blocks == 1
                ? MM."<gray>Resizing by: <dark_aqua>\{NumberFmt.NO_DECIMAL.format(blocks)} block"
                : MM."<gray>Resizing by: <dark_aqua>\{NumberFmt.NO_DECIMAL.format(blocks)} blocks";
    }

    private static NumberInput createInput(SelectionExpanderMenu menu) {
        SelectionExpanderTool tool = menu.getTool();
        ItemRef itemRef = menu.getItemRef();

        return new NumberInput(
                Material.TRIDENT, NamedTextColor.DARK_AQUA, null,
                "Number of Blocks",
                new Lore().wrap("<gray>How many to resize the selection by.").asList(),
                null, ToolDataType.ITEM_BOUND,
                1, MIN, MAX,
                _ -> (double) tool.getItemBound(itemRef).getDistance(),
                (player, value) -> tool.editItemBound(player, itemRef, s -> s.setDistance(value.intValue()))
        );
    }
}
