package io.kloon.gameserver.modes.creative.tools.impl.erosion.menu;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.legacy.NumberInput;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.legacy.NumberInputButton;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.ErosionTool;
import io.kloon.gameserver.modes.creative.tools.impl.erosion.params.ErosionParams;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.Material;

import java.util.function.BiFunction;
import java.util.function.Function;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ErosionParamButton extends NumberInputButton {
    public ErosionParamButton(int slot, ErosionToolMenu menu, String label, Material icon, int max, Lore lore,
                              Function<ErosionParams, Double> get,
                              BiFunction<ErosionParams, Double, ErosionParams> edit) {
        super(slot, createInput(menu, label, icon, max, lore, get, edit));
    }

    @Override
    public Component getYourLine(CreativePlayer player, double playerValue) {
        return MM."<gray>Setting: <aqua>\{formatValue(playerValue)}";
    }

    private static NumberInput createInput(ErosionToolMenu menu, String label, Material icon, int max, Lore lore,
                                           Function<ErosionParams, Double> get,
                                           BiFunction<ErosionParams, Double, ErosionParams> edit) {
        ErosionTool tool = menu.getTool();
        ItemRef itemRef = menu.getItemRef();

        return new NumberInput(icon, NamedTextColor.AQUA, null,
                label, lore.asList(),
                null, ToolDataType.ITEM_BOUND,
                1, 1, max,
                _ -> get.apply(tool.getItemBound(itemRef).getParams()),
                (p, value) -> tool.editItemBound(p, itemRef, s -> {
                    ErosionParams params = s.getParams();
                    ErosionParams edited = edit.apply(params, value);
                    s.setParams(edited);
                }));
    }
}
