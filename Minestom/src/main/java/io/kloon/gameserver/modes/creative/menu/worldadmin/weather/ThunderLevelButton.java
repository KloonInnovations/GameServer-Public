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

public class ThunderLevelButton implements ChestButton {
    public static final double MIN_THUNDER = 0;
    public static final double MAX_THUNDER = 1.0;

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        String[] displayLines = SignUX.inputLines("Thunder level", MIN_THUNDER, MAX_THUNDER, NumberFmt.TWO_DECIMAL);
        SignUX.display(player, displayLines, new SignUXNumberInput().bounds(MIN_THUNDER, MAX_THUNDER).build(player, input -> {
            double mcThunderLevel = input;
            WeatherCommand.setThunderLevel(player, mcThunderLevel);
            ChestMenuInv.rerender(player);
        }));
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Thunder Level";

        Lore lore = new Lore();
        lore.wrap("<gray>How intense the thunderstorm.");
        lore.addEmpty();
        lore.wrap("<dark_gray>Does not make thunder strike. This setting is a shader effect.");
        lore.addEmpty();

        float thunderLevel = player.getInstance().getWeather().thunderLevel();
        lore.add(MM."<gray>Thunderstorm level: <yellow>\{NumberFmt.TWO_DECIMAL.format(thunderLevel)}");
        lore.addEmpty();

        lore.add("<cta>Click to edit!");

        return MenuStack.of(Material.HORN_CORAL_FAN, name, lore);
    }
}
