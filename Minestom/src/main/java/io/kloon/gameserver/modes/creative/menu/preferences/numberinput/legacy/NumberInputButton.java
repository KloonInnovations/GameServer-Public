package io.kloon.gameserver.modes.creative.menu.preferences.numberinput.legacy;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.signui.input.SignUXNumberInput;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public abstract class NumberInputButton implements ChestButton {
    private static final Logger LOG = LoggerFactory.getLogger(NumberInputButton.class);

    private final int slot;
    protected final NumberInput number;

    public NumberInputButton(int slot, NumberInput number) {
        this.slot = slot;
        this.number = number;
    }

    @Override
    public final void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        double playerValue = number.getValue().apply(player);
        if (playerValue != number.defaultValue() && click.isRightClick() && canRightClickToReset()) {
            setValue(player, number.defaultValue());
            ChestMenuInv.rerenderButton(slot, player);
            return;
        }

        double min = number.min();
        double max = number.max();

        String[] displayLines = SignUX.inputLines(getSignLine(), min, max, getNumberFormat());
        SignUX.display(player, displayLines, new SignUXNumberInput().bounds(min, max).build(player, input -> {
            setValue(player, input);
        }));
    }

    public void setValue(CreativePlayer player, double value) {
        try {
            double before = number.getValue().apply(player);
            number.setValue().accept(player, value);
            String colorHex = number.textColor().asHexString();
            onSetValue(player, colorHex, before, value);
        } catch (Throwable t) {
            LOG.error("Error with input handling", t);
            player.sendPitError(MM."<gray>Error while handling your input!");
            player.closeInventory();
        }
    }

    public Component getYourLine(CreativePlayer player, double playerValue) {
        return MM."<gray>\{number.name()}: \{formatValue(playerValue)}";
    }

    public void onSetValue(CreativePlayer player, String colorHex, double before, double after) {
        player.playSound(SoundEvent.BLOCK_NOTE_BLOCK_BELL, 1.1, 0.7);
        if (number.iconText() == null) {
            player.sendPit(number.textColor(), number.name(), MM."<gray>Adjusted from \{formatValue(before)} to <\{colorHex}>\{formatValue(after)}<gray>!");
        } else {
            player.sendPit(number.textColor(), number.name(), MM."<gray>Adjusted from \{formatValue(before)} to <\{colorHex}>\{formatValue(after)} \{number.iconText()}<gray>!");
        }
    }

    public String formatValue(double value) {
        return NumberFmt.NO_DECIMAL.format(value);
    }

    public boolean showDefault() {
        return true;
    }

    public NumberFormat getNumberFormat() {
        return NumberFmt.ONE_DECIMAL;
    }

    public String getSignLine() {
        return "Enter Value";
    }

    @Override
    public final ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        String colorHex = number.textColor().asHexString();
        String iconText = number.iconText();

        Component name = iconText == null
                ? MM."<\{colorHex}>Edit \{number.name()}"
                : MM."<\{colorHex}>\{iconText} <title>Edit \{number.name()}";

        List<Component> lore = new ArrayList<>();
        if (number.dataType() != null) {
            lore.add(number.dataType().getLoreSubtitle());
        }
        if (number.commandLabel() != null) {
            lore.add(MM."<cmd>\{number.commandLabel()}");
        }
        if (!lore.isEmpty()) {
            lore.add(Component.empty());
        }

        lore.addAll(number.lore());
        lore.add(Component.empty());

        double playerValue = number.getValue().apply(player);
        lore.add(getYourLine(player, playerValue));
        if (playerValue != number.defaultValue() && showDefault()) {
            lore.addAll(MM_WRAP."<dark_gray>Default = \{formatValue(number.defaultValue())}");
        }

        Lore extraLore = createExtraLore(player, playerValue);
        if (extraLore != null) {
            lore.addAll(extraLore.asList());
        }

        lore.add(Component.empty());
        if (playerValue != number.defaultValue() && canRightClickToReset()) {
            lore.add(MM."<rcta>Click to reset!");
            lore.add(MM."<lcta>Click to edit!");
        } else {
            lore.add(MM."<cta>Click to edit!");
        }

        return MenuStack.of(number.iconMat())
                .name(name)
                .lore(lore)
                .build();
    }

    protected Lore createExtraLore(CreativePlayer player, double playerValue) {
        return null;
    }

    protected boolean canRightClickToReset() {
        return true;
    }
}
