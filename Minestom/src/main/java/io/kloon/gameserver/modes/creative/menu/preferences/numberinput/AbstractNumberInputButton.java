package io.kloon.gameserver.modes.creative.menu.preferences.numberinput;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.signui.input.SignUXNumberInput;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public abstract class AbstractNumberInputButton<Data> implements ChestButton {
    protected final int slot;
    protected final ToolDataType dataType;
    protected final NumberInput<Data> number;

    private String shortHand = null;

    public AbstractNumberInputButton(int slot, ToolDataType dataType, NumberInput<Data> number) {
        this.slot = slot;
        this.dataType = dataType;
        this.number = number;
    }

    public AbstractNumberInputButton<Data> withShortHand(String shorthand) {
        this.shortHand = shorthand;
        return this;
    }

    @Override
    public final void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        double playerValue = getValue(player);
        if (playerValue != number.defaultValue() && click.isRightClick() && canRightClickToReset()) {
            setValue(player, number.defaultValue());
            ChestMenuInv.rerenderButton(slot, player);
            return;
        }

        double min = number.min();
        double max = number.max();

        String[] displayLines = SignUX.inputLines("Enter Value", min, max, NumberFmt.ONE_DECIMAL);
        SignUX.display(player, displayLines, new SignUXNumberInput().bounds(min, max).build(player, input -> {
            setValue(player, input);
        }));
    }

    public Component getYourLine(CreativePlayer player, double playerValue) {
        if (shortHand == null) {
            return MM."<gray>\{number.name()}: <\{number.textColor().asHexString()}>\{formatValue(playerValue)}";
        } else {
            return MM."<gray>\{shortHand}: <\{number.textColor().asHexString()}>\{formatValue(playerValue)}";
        }
    }

    public String formatValue(double value) {
        return NumberFmt.NO_DECIMAL.format(value);
    }

    protected abstract double getValue(CreativePlayer player);

    protected abstract void setValue(CreativePlayer player, double value);

    @Override
    public final ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        String colorHex = number.textColor().asHexString();

        Component name = MM."<\{colorHex}>Edit \{number.name()}";

        List<Component> lore = new ArrayList<>();
        lore.add(dataType.getLoreSubtitle());
        lore.add(Component.empty());

        lore.addAll(number.lore());
        lore.add(Component.empty());

        double playerValue = getValue(player);
        lore.add(getYourLine(player, playerValue));
        if (playerValue != number.defaultValue()) {
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
