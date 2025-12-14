package io.kloon.gameserver.modes.creative.tools.generics;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ToolMessages {
    public static void sendRequireSelection(CreativePlayer player) {
        player.sendPit(NamedTextColor.RED, "WOAH THERE!", MM."<gray>You need a selection before using this tool!");
        player.playSound(SoundEvent.ENTITY_BEE_HURT, 1.0);
    }

    public static void sendRequireConfiguration(CreativePlayer player) {
        player.sendPit(NamedTextColor.GOLD, "REQUIRES CONFIG", MM."<gray>This tool requires configuration!");
        player.sendPit(NamedTextColor.YELLOW, "TIP", MM."<gray>Press F (item swap) to configure!");
        player.playSound(SoundEvent.ENTITY_BEE_HURT, 0.7);
    }
}
