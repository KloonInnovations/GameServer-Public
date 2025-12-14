package io.kloon.gameserver.modes.creative.ux.messaging;

import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CreativeBroadcasterWithInitiator {
    private final CreativePlayer player;
    private final CreativeBroadcaster broadcaster;

    public CreativeBroadcasterWithInitiator(CreativePlayer player, CreativeBroadcaster broadcaster) {
        this.player = player;
        this.broadcaster = broadcaster;
    }

    public SentMessage send(MsgCat category, TextColor subjectColor, String subject, Component details) {
        return broadcaster.send(player, category, subjectColor, subject, details);
    }

    public SentMessage send(MsgCat category, TextColor subjectColor, String subject, Component details, SoundEvent sound, Pitch pitch) {
        return broadcaster.send(player, category, subjectColor, subject, details, sound, pitch);
    }

    public SentMessage send(MsgCat category, TextColor subjectColor, String subject, Component details, SoundEvent sound, Pitch pitch, double volume) {
        return broadcaster.send(player, category, subjectColor, subject, details, sound, pitch, volume);
    }

    public SentMessage send(MsgCat category, TextColor subjectColor, String subject, Component details, SoundEvent sound, double pitch) {
        return broadcaster.send(player, category, subjectColor, subject, details, sound, pitch);
    }

    public SentMessage send(MsgCat category, TextColor subjectColor, String subject, Component details, CoolSound sound) {
        return broadcaster.send(player, category, subjectColor, subject, details, sound.soundEvent(), sound.pitch());
    }

    public SentMessage sendExcept(@Nullable Player exceptPlayer, MsgCat category, TextColor subjectColor, String subject, Component details, SoundEvent sound, double pitch) {
        Set<UUID> exceptRecipients = new HashSet<>();
        if (exceptPlayer != null) {
            exceptRecipients.add(exceptPlayer.getUuid());
        }
        return broadcaster.send(player, category, subjectColor, subject, details, sound, Pitch.base(pitch), 1.0, exceptRecipients);
    }
}
