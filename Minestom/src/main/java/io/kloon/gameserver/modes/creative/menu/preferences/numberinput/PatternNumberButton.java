package io.kloon.gameserver.modes.creative.menu.preferences.numberinput;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.menu.EditPatternMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;

public class PatternNumberButton<Pattern extends CreativePattern> extends AbstractNumberInputButton<Pattern> {
    private final EditPatternMenu<Pattern> menu;

    public PatternNumberButton(int slot, EditPatternMenu<Pattern> menu, NumberInput<Pattern> number) {
        super(slot, ToolDataType.PATTERN_BOUND, number);
        this.menu = menu;
    }

    @Override
    protected double getValue(CreativePlayer player) {
        Pattern pattern = menu.getPattern();
        return number.getValue().apply(pattern);
    }

    @Override
    protected void setValue(CreativePlayer player, double value) {
        Pattern pattern = menu.getPattern();
        pattern = number.editValue().apply(pattern, value);
        menu.updateAndDisplay(player, pattern);
    }
}
