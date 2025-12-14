package io.kloon.gameserver.modes.creative.commands.world;

import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.CreativeExecutor;
import io.kloon.gameserver.modes.creative.menu.worldadmin.WorldAdminMenu;
import io.kloon.gameserver.modes.creative.menu.worldadmin.time.TimeChange;
import io.kloon.gameserver.modes.creative.menu.worldadmin.time.WorldTimeMenu;
import io.kloon.gameserver.modes.creative.menu.worldadmin.time.preset.PresetTime;
import io.kloon.gameserver.modes.creative.storage.datainworld.world.CreativeTimeStorage;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.modes.creative.ux.messaging.SentMessage;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.number.ArgumentDouble;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class TimeCommand extends Command {
    public static final String LABEL = "time";

    public TimeCommand() {
        super(LABEL);

        setDefaultExecutor(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                WorldAdminMenu adminMenu = new WorldAdminMenu(player.createMainMenu());
                new WorldTimeMenu(adminMenu).display(player);
            }
        });

        ArgumentEnum<PresetTime> presetArg = ArgumentType.Enum("time preset", PresetTime.class);
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                PresetTime preset = context.get(presetArg);
                setTime(player, preset.getMcTime());
            }
        }, ArgumentType.Literal("set"), presetArg);

        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                long time = player.getInstance().getTime() % 24_000;
                player.sendPit(NamedTextColor.GREEN, "TIME!", MM."<gray>Current world time: <green>\{time}");
            }
        }, ArgumentType.Literal("get"));

        ArgumentInteger timeArg = ArgumentType.Integer("time (between 0 and 24000)");
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                setTime(player, context.get(timeArg));
            }
        }, ArgumentType.Literal("set"), timeArg);

        ArgumentDouble rateArg = ArgumentType.Double("rate (time per second)");
        addSyntax(new CreativeExecutor() {
            @Override
            public void apply(@NotNull CreativePlayer player, @NotNull CommandContext context) {
                double rate = context.get(rateArg) / 20;
                setRate(player, rate);
            }
        }, ArgumentType.Literal("setrate"), rateArg);
    }

    public static void setTime(CreativePlayer player, int time) {
        if (!player.canEditWorld(true)) {
            return;
        }

        CreativeTimeStorage timeStorage = player.getInstance().getWorldStorage().getTime();

        TimeChange.Time timeBefore = new TimeChange.Time(timeStorage.getTime(), timeStorage.getTimeRate());

        timeStorage.setTime(time);
        player.getInstance().setTime(time);

        TimeChange.Time timeAfter = new TimeChange.Time(timeStorage.getTime(), timeStorage.getTimeRate());

        SentMessage msg = player.broadcast().send(MsgCat.WORLD,
                NamedTextColor.GREEN, "TIME", MM."<gray>Set to <green>\{time}<gray>!",
                SoundEvent.ENTITY_WOLF_WHINE, 2.0 - (time / 1000.0) * (0.9 / 24));

        player.addToHistory(CreativeToolType.TIME, "<green>Set World Time", msg, new TimeChange(timeBefore, timeAfter));
    }

    public static void setRate(CreativePlayer player, double timeRate) {
        if (!player.canEditWorld(true)) {
            return;
        }
        timeRate = Math.clamp(timeRate, -24_000, 24_000);

        CreativeTimeStorage timeStorage = player.getInstance().getWorldStorage().getTime();

        TimeChange.Time timeBefore = new TimeChange.Time(timeStorage.getTime(), timeStorage.getTimeRate());

        timeStorage.setTimeRate(timeRate);

        TimeChange.Time timeAfter = new TimeChange.Time(timeStorage.getTime(), timeStorage.getTimeRate());

        SentMessage msg = player.broadcast().send(MsgCat.WORLD,
                NamedTextColor.GREEN, "TIME", MM."<gray>Set time rate to <aqua>\{NumberFmt.TWO_DECIMAL.format(timeRate * 20)}/s<gray>!",
                SoundEvent.ENTITY_GOAT_SCREAMING_AMBIENT, Pitch.rng(1.8, 0.2), 0.6);

        player.addToHistory(CreativeToolType.TIME, "<green>Set World Time Rate", msg, new TimeChange(timeBefore, timeAfter));
    }
}
