package io.kloon.gameserver.modes.creative.menu.preferences;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.legacy.NumberInput;
import io.kloon.gameserver.modes.creative.menu.preferences.numberinput.legacy.NumberInputButton;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.hand.PlayerChangesByHand;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.sound.SoundEvent;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class HandBufferingTimeButton extends NumberInputButton {
    public static final String ICON = "✋";

    private static final List<Component> LORE = new ArrayList<>();
    static {
        LORE.addAll(MM_WRAP."<gray>How long to buffer changes by hand before flushing them to history.");
        LORE.add(Component.empty());
        LORE.addAll(MM_WRAP."<gray>If you don't know what this means, comfort yourself knowing we have no clue either.");
    }

    public static final NumberInput INPUT = new NumberInput(
            CreativeToolType.HAND.getMaterial(), NamedTextColor.DARK_GREEN, ICON,
            "Hand Buffering Time", LORE, null, ToolDataType.PLAYER_BOUND,
            PlayerChangesByHand.DEFAULT_DURATION_SECONDS, 0.2, 10.0,
            p -> p.getCreativeStorage().getHandBufferingTicks() * 0.05,
            (p, valueSeconds) -> p.getCreativeStorage().setHandBufferingTicks((int) Math.round(valueSeconds * 20))
    );

    public HandBufferingTimeButton(int slot) {
        super(slot, INPUT);
    }

    @Override
    public Component getYourLine(CreativePlayer player, double playerValue) {
        return MM."<gray>Buffer time: <green>\{formatValue(playerValue)}";
    }

    @Override
    public void onSetValue(CreativePlayer player, String colorHex, double before, double after) {
        player.getChangesByHand().setBufferingTicks(player.getCreativeStorage().getHandBufferingTicks());

        player.msg().send(MsgCat.PREFERENCE,
                NamedTextColor.DARK_GREEN, "BUFFER TIME", MM."<gray>Adjusted from \{formatValue(before)} to <\{colorHex}>\{formatValue(after)} \{number.iconText()}<gray>!",
                SoundEvent.BLOCK_NOTE_BLOCK_BELL, 1.1, 0.7);
    }

    @Override
    public String formatValue(double value) {
        return getNumberFormat().format(value) + "s";
    }
}
