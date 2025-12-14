package io.kloon.gameserver.modes.creative.commands.tools.api.give;

import io.kloon.gameserver.minestom.sounds.Pitch;
import net.kyori.adventure.text.Component;
import net.minestom.server.sound.SoundEvent;

public record ToolEditFx(
        Component details,
        SoundEvent sound,
        Pitch pitch
) {
    public ToolEditFx(Component details, SoundEvent sound, double pitch) {
        this(details, sound, Pitch.base(pitch));
    }
}
