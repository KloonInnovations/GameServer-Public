package io.kloon.gameserver.modes.creative.ux.messaging;

import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

public record SentMessage(
        TextColor subjectColor,
        String subject,
        Component details,
        @Nullable SoundEvent soundEvent,
        Pitch pitch,
        double volume
) {
    public CoolSound sound() {
        if (soundEvent == null) {
            return new CoolSound(SoundEvent.UI_BUTTON_CLICK, pitch, volume);
        }

        return new CoolSound(soundEvent, pitch, volume);
    }
}
