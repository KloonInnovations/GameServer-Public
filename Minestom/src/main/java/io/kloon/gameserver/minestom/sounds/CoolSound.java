package io.kloon.gameserver.minestom.sounds;

import net.minestom.server.sound.SoundEvent;

public record CoolSound(
        SoundEvent soundEvent,
        Pitch pitch,
        double volume
) {
    public CoolSound(SoundEvent soundEvent) {
        this(soundEvent, 1.0);
    }

    public CoolSound(SoundEvent soundEvent, double pitch) {
        this(soundEvent, Pitch.base(pitch), 1.0);
    }

    public CoolSound(SoundEvent soundEvent, Pitch pitch) {
        this(soundEvent, pitch, 1.0);
    }
}
