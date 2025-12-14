package io.kloon.gameserver.modes.creative.menu.worldadmin.weather;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.commands.world.TimeCommand;
import io.kloon.gameserver.modes.creative.commands.world.WeatherCommand;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.Weather;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.PotionContents;
import net.minestom.server.potion.PotionType;

import java.text.NumberFormat;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WeatherMenu extends ChestMenu {
    public static final String ICON = "\uD83C\uDF26"; // 🌦

    private final ChestMenu parent;

    public WeatherMenu(ChestMenu parent) {
        super("Weather", ChestSize.FOUR);
        this.parent = parent;
    }

    @Override
    protected void registerButtons() {
        reg(11, new ToggleRainButton());
        reg(13, new RainLevelButton());
        reg(15, new ThunderLevelButton());

        reg().goBack(parent);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Instance instance = player.getInstance();
        long time = instance.getTime() % 24_000;
        boolean night = time > 12_000;

        String iconColor = night ? "aqua" : "yellow";

        Component name = MM."<\{iconColor}>\{ICON} <title>Weather";

        Lore lore = new Lore();
        lore.add(MM."<cmd>\{WeatherCommand.LABEL}");
        lore.addEmpty();
        lore.wrap("<gray>Adjust the rain and thunder in this world.");
        lore.addEmpty();

        Weather weather = instance.getWeather();
        if (weather.rainLevel() == 0) {
            lore.add(night ? MM."<gray>Weather: <aqua>Moonlit" : MM."<gray>Weather: <gold>Sunny");
        } else {
            NumberFormat fmt = NumberFmt.TWO_DECIMAL;
            lore.add(MM."<gray>Weather: <blue>Rainy <dark_gray>(\{fmt.format(weather.rainLevel())})");
            if (weather.thunderLevel() > 0) {
                lore.add(MM."<gray>Thunder: <yellow>(\{fmt.format(weather.thunderLevel())})");
            }
        }

        lore.addEmpty();
        lore.add("<cta>Click to edit!");

        Material icon;
        if (weather.rainLevel() == 0) {
            icon = Material.TIPPED_ARROW;
        } else {
            icon = night ? Material.CHORUS_FRUIT : Material.SUNFLOWER;
        }

        return MenuStack.of(icon)
                .name(name)
                .lore(lore)
                .set(DataComponents.POTION_CONTENTS, new PotionContents(PotionType.SWIFTNESS))
                .build();
    }
}
