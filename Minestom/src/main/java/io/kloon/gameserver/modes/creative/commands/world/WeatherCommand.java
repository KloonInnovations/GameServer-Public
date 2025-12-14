package io.kloon.gameserver.modes.creative.commands.world;

import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.menu.worldadmin.WorldAdminMenu;
import io.kloon.gameserver.modes.creative.menu.worldadmin.weather.RainLevelButton;
import io.kloon.gameserver.modes.creative.menu.worldadmin.weather.ThunderLevelButton;
import io.kloon.gameserver.modes.creative.menu.worldadmin.weather.WeatherChange;
import io.kloon.gameserver.modes.creative.menu.worldadmin.weather.WeatherMenu;
import io.kloon.gameserver.modes.creative.storage.datainworld.world.CreativeWeatherStorage;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.modes.creative.ux.messaging.SentMessage;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentDouble;
import net.minestom.server.instance.Weather;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WeatherCommand extends Command {
    public static final String LABEL = "weather";

    public WeatherCommand() {
        super(LABEL);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                WorldAdminMenu adminMenu = new WorldAdminMenu(player.createMainMenu());
                new WeatherMenu(adminMenu).display(player);
            }
        });

        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                boolean raining = player.getInstance().getWorldStorage().getWeather().getRainLevel() != 0;
                setRaining(player, !raining);
            }
        }, ArgumentType.Literal("rain"));

        ArgumentDouble rainLevelArg = ArgumentType.Double(STR."rain level (between \{(int) RainLevelButton.MIN_RAIN} and \{(int) RainLevelButton.MAX_RAIN}");
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                double rainLevel = context.get(rainLevelArg);
                rainLevel = Math.clamp(rainLevel, RainLevelButton.MIN_RAIN, RainLevelButton.MAX_RAIN);
                setRainLevel(player, rainLevel);
            }
        }, ArgumentType.Literal("rain"), rainLevelArg);

        ArgumentDouble thunderLevelArg = ArgumentType.Double(STR."thunder level (between \{(int) ThunderLevelButton.MIN_THUNDER} and \{(int) ThunderLevelButton.MAX_THUNDER}");
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                double thunderLevel = context.get(thunderLevelArg);
                thunderLevel = Math.clamp(thunderLevel, ThunderLevelButton.MIN_THUNDER, ThunderLevelButton.MAX_THUNDER);
                setThunderLevel(player, thunderLevel);
            }
        }, ArgumentType.Literal("thunder"), thunderLevelArg);
    }

    public static void setRaining(CreativePlayer player, boolean raining) {
        if (!player.canEditWorld(true)) return;

        CreativeInstance instance = player.getInstance();

        CreativeWeatherStorage weatherStorage = instance.getWorldStorage().getWeather();
        Weather weatherBefore = weatherStorage.asWeather();

        SentMessage msg;
        if (raining) {
            weatherStorage.setRainLevel(1f);


            msg = player.broadcast().send(MsgCat.WORLD,
                    NamedTextColor.BLUE, "RAIN!", MM."<gray>Set to raining with rain level <aqua>\{NumberFmt.TWO_DECIMAL.format(1f)}<gray>!",
                    SoundEvent.ENTITY_DOLPHIN_SPLASH, 2.0);
        } else {
            weatherStorage.setRainLevel(0f);

            msg = player.broadcast().send(MsgCat.WORLD,
                    NamedTextColor.BLUE, "RAIN!", MM."<gray>Disabled rain with rain level <aqua>\{NumberFmt.TWO_DECIMAL.format(0f)}<gray>!",
                    SoundEvent.ENTITY_DOLPHIN_SPLASH, 0.5);
        }

        Weather weatherAfter = weatherStorage.asWeather();
        instance.setWeather(weatherStorage.asWeather(), 1);

        player.addToHistory(CreativeToolType.WEATHER, "<aqua>Toggled Rain", msg, new WeatherChange(weatherBefore, weatherAfter));
    }

    public static void setRainLevel(CreativePlayer player, double mcRainLevel) {
        if (!player.canEditWorld(true)) return;
        mcRainLevel = Math.clamp(mcRainLevel, 0.0, 1.0);

        CreativeInstance instance = player.getInstance();

        CreativeWeatherStorage weatherStorage = instance.getWorldStorage().getWeather();
        Weather weatherBefore = weatherStorage.asWeather();

        weatherStorage.setRainLevel((float) mcRainLevel);
        Weather weatherAfter = weatherStorage.asWeather();
        instance.setWeather(weatherAfter, 1);

        SentMessage msg = player.msg().send(MsgCat.WORLD,
                NamedTextColor.BLUE, "RAIN LEVEL!", MM."<gray>Set to <aqua>\{NumberFmt.TWO_DECIMAL.format(mcRainLevel)}<gray>!",
                SoundEvent.ENTITY_DOLPHIN_SPLASH, Pitch.range(mcRainLevel, -1, 10));

        player.addToHistory(CreativeToolType.WEATHER, "<aqua>Set Rain Level", msg, new WeatherChange(weatherBefore, weatherAfter));
    }

    public static void setThunderLevel(CreativePlayer player, double mcThunderLevel) {
        if (!player.canEditWorld(true)) return;
        mcThunderLevel = Math.clamp(mcThunderLevel, 0.0, 1.0);

        CreativeInstance instance = player.getInstance();

        CreativeWeatherStorage weatherStorage = instance.getWorldStorage().getWeather();
        Weather weatherBefore = weatherStorage.asWeather();

        weatherStorage.setThunderLevel((float) mcThunderLevel);
        Weather weatherAfter = weatherStorage.asWeather();
        instance.setWeather(weatherAfter, 1);

        SentMessage msg = player.msg().send(MsgCat.WORLD,
                NamedTextColor.YELLOW, "THUNDER LEVEL!", MM."<gray>Set to <yellow>\{NumberFmt.TWO_DECIMAL.format(mcThunderLevel)}<gray>!",
                SoundEvent.ITEM_TRIDENT_THUNDER, 1.9, 0.3);

        player.addToHistory(CreativeToolType.WEATHER, "<yellow>Set Thunder Level", msg, new WeatherChange(weatherBefore, weatherAfter));
    }
}
