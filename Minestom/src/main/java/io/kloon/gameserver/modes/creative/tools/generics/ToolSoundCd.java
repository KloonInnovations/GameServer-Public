package io.kloon.gameserver.modes.creative.tools.generics;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.util.cooldowns.impl.TickCooldown;
import io.kloon.gameserver.util.cooldowns.maps.PlayerTickCooldownMap;
import net.minestom.server.sound.SoundEvent;

public class ToolSoundCd {
    private final PlayerTickCooldownMap cd = new PlayerTickCooldownMap(18);

    public void play(CreativePlayer player, ToolClick click, SoundEvent sound, Pitch pitch) {
        play(player, click, sound, pitch.compute());
    }

    public void play(CreativePlayer player, ToolClick click, SoundEvent sound, double pitch) {
        TickCooldown playerCd = cd.get(player);
        if (!playerCd.isOnCooldown()) {
            player.playSound(sound, pitch);
        }
        if (click.isRightClick()) {
            playerCd.cooldown();
        }
    }
}
