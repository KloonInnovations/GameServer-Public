package io.kloon.gameserver.modes.creative.patterns.impl.grid.menu;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.signui.input.SignUXNumberInput;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.patterns.impl.grid.GridAxis;
import io.kloon.gameserver.modes.creative.patterns.impl.grid.GridPattern;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.util.coordinates.Axis;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class SetAllAxisSpacingButton implements ChestButton {
    private final GridPatternMenu menu;

    public SetAllAxisSpacingButton(GridPatternMenu menu) {
        this.menu = menu;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        int min = 1;
        int max = GridAxis.MAX_SPACING;

        String[] displayLines = SignUX.inputLines("Enter Value", min, max, NumberFmt.ONE_DECIMAL);
        SignUX.display(player, displayLines, new SignUXNumberInput().bounds(min, max).build(player, input -> {
            setValue(player, input.intValue());
        }));
    }

    private void setValue(CreativePlayer player, int value) {
        GridPattern pattern = menu.getPattern();

        for (Axis axis : Axis.values()) {
            GridAxis gridAxis = pattern.getAxis(axis).withSpacing(value);
            pattern = pattern.withAxis(gridAxis);
        }

        menu.updateAndDisplay(player, pattern);

        ToolDataType.PATTERN_BOUND.sendMsg(player, MM."<gray>Set all grid axis spacings to <green>\{value}<gray>!",
                SoundEvent.BLOCK_NOTE_BLOCK_BELL, 1.1, 0.7);
    }

    @Override
    public ItemStack renderButton(Player p) {
        Component name = MM."<title>Edit All Spacing";

        Lore lore = new Lore();
        lore.add(ToolDataType.PATTERN_BOUND.getLoreSubtitle());
        lore.addEmpty();

        lore.wrap("<gray>Set the spacing on every axis.");
        lore.addEmpty();

        lore.add("<cta>Click to edit!");

        return MenuStack.of(Material.KELP, name, lore);
    }
}
