package io.kloon.gameserver.modes.creative.menu.worldadmin.weather;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.signui.input.SignUXNumberInput;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.world.WeatherCommand;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class RainLevelButton implements ChestButton {
    public static final double MIN_RAIN = 0;
    public static final double MAX_RAIN = 1.0;

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        String[] displayLines = SignUX.inputLines("Rain level", STR."\{(int) MIN_RAIN} to \{(int) MAX_RAIN}");
        SignUX.display(player, displayLines, new SignUXNumberInput().bounds(MIN_RAIN, MAX_RAIN).build(player, input -> {
            WeatherCommand.setRainLevel(player, input);
            ChestMenuInv.rerender(player);
        }));
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Rain Level";

        Lore lore = new Lore();
        lore.wrap("<gray>How much rain is falling from the sky.");
        lore.addEmpty();
        lore.wrap("<dark_gray>Value between 0.0 (dry) and 1.0 (downpour).");
        lore.addEmpty();

        float rainLevel = player.getInstance().getWeather().rainLevel();
        lore.add(MM."<gray>Rain level: <blue>\{NumberFmt.TWO_DECIMAL.format(rainLevel)}");
        lore.addEmpty();

        lore.add("<cta>Click to edit!");

        return MenuStack.of(Material.WATER_BUCKET, name, lore);
    }
}
