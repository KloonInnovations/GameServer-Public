package io.kloon.gameserver.modes.creative.tools.snipe.settings.menu;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.snipe.SnipeCommand;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.legacy.NumberInput;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.legacy.NumberInputButton;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.SnipePlayerStorage;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class RangeInputButton extends NumberInputButton {
    public static final NumberInput INPUT = new NumberInput(
            Material.CROSSBOW, NamedTextColor.GREEN, null, "Snipe Range",
            MM_WRAP."<gray>How many blocks away your snipe target is.",
            STR."\{SnipeCommand.LABEL} <range>", ToolDataType.PLAYER_BOUND,
            SnipePlayerStorage.DEFAULT_RANGE,
            0, 80,
            p -> p.getCreativeStorage().getSnipe().getRange(),
            (p, range) -> p.getCreativeStorage().getSnipe().setRange(range));

    public RangeInputButton(int slot) {
        super(slot, INPUT);
    }

    @Override
    public Component getYourLine(CreativePlayer player, double playerValue) {
        return MM."<gray>Range: \{formatValue(playerValue)}";
    }

    @Override
    public String formatValue(double value) {
        return STR."<green>\{NumberFmt.ONE_DECIMAL.format(value)} blocks";
    }

    @Override
    public boolean showDefault() {
        return false;
    }

    @Override
    public void onSetValue(CreativePlayer player, String colorHex, double before, double after) {
        player.msg().send(MsgCat.TOOL,
                number.textColor(), number.name(), MM."<gray>Adjusted from \{formatValue(before)} to <\{colorHex}>\{formatValue(after)}<gray>!");
        player.getSnipe().playRangeSound(after);
    }
}
