package io.kloon.gameserver.chestmenus.signui.input;

import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.util.input.NumberParsing;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.sound.SoundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class SignUXNumberInput {
    private static final Logger LOG = LoggerFactory.getLogger(SignUXNumberInput.class);

    private double min = 0.0;
    private double max = 100.0;
    private boolean reOpenMenu = true;
    private Consumer<Player> onError = p -> {};

    public SignUXNumberInput min(double min) {
        this.min = min;
        return this;
    }

    public SignUXNumberInput max(double max) {
        this.max = max;
        return this;
    }

    public SignUXNumberInput bounds(double min, double max) {
        if (min <= max) {
            this.min = min;
            this.max = max;
        } else {
            this.min = max;
            this.max = min;
        }
        return this;
    }

    public SignUXNumberInput onError(Consumer<Player> consumer) {
        this.onError = consumer;
        return this;
    }

    public SignUXNumberInput reOpenMenu(boolean reOpen) {
        this.reOpenMenu = reOpen;
        return this;
    }

    public Consumer<String[]> build(Player player, Consumer<Double> consumer) {
        return lines -> {
            double num;
            try {
                num = NumberParsing.parseDouble(lines[0]);
            } catch (Throwable t) {
                error(player, MM."<red><b>OOPS!</b></red> <gray>Whatever you entered wasn't a number!");
                return;
            }

            if (Double.isNaN(num) || !Double.isFinite(num)) {
                error(player, MM."<red><b>WOAH!</b></red> <gray>Invalid number! What's wrong with you?");
                return;
            }

            if (num < min) {
                error(player, MM."<red><b>TOO SMALL!</b></red> <gray>Input is below the minimum of <red>\{min}<gray>!");
                return;
            }
            if (num > max) {
                error(player, MM."<red><b>TOO BIG!</b></red> <gray>Input is over the maximum of <red>\{max}<gray>!");
                return;
            }

            try {
                consumer.accept(num);
                maybeReOpenMenu(player);
            } catch (Throwable t) {
                player.sendMessage(MM."<dark_red><b>ERROR!</b></dark_red> <gray>There was an error while processing your input!");
                LOG.error("Error in sign menu number input consumer", t);
            }
        };
    }

    public void display(CreativePlayer player, String signLine, Consumer<Double> consumer) {
        String[] displayLines = SignUX.inputLines(signLine, min, max);
        SignUX.display(player, displayLines, build(player, consumer));
    }

    private void error(Player player, Component msg) {
        player.sendMessage(msg);
        player.playSound(Sound.sound(SoundEvent.ENTITY_VILLAGER_NO, Sound.Source.PLAYER, 1f, 1.1f));

        try {
            onError.accept(player);
            maybeReOpenMenu(player);
        } catch (Throwable t) {
            LOG.error("Error in error handler in sign menu number input", t);
        }
    }

    private void maybeReOpenMenu(Player player) {
        if (reOpenMenu) {
            if (player.getOpenInventory() instanceof ChestMenuInv chestInv) {
                chestInv.getMenu().display(player);
            }
        }
    }
}
