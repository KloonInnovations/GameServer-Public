package io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.menu;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.ItemBoundNumberButton;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.NumberInput;
import io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.PyramidToolSettings;
import net.kyori.adventure.text.Component;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PyramidStepsButton extends ItemBoundNumberButton<PyramidToolSettings> {
    public PyramidStepsButton(int slot, PyramidToolMenu menu, NumberInput<PyramidToolSettings> number) {
        super(slot, menu, number);
    }

    @Override
    public Component getYourLine(CreativePlayer player, double playerValue) {
        return MM."<gray>Steps: <yellow>\{formatValue(playerValue)}";
    }

    @Override
    protected Lore createExtraLore(CreativePlayer player, double playerValue) {
        if (playerValue < 30) {
            return super.createExtraLore(player, playerValue);
        }
        return new Lore().wrap("<dark_gray>Phew... that's a lot of steps!");
    }
}
