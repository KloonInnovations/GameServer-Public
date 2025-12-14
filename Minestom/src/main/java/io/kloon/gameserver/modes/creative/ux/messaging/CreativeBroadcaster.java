package io.kloon.gameserver.modes.creative.ux.messaging;

import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.storage.playerdata.MessagingStorage;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CreativeBroadcaster {
    private final CreativeInstance instance;

    public CreativeBroadcaster(CreativeInstance instance) {
        this.instance = instance;
    }

    public SentMessage send(@Nullable Player initiator, MsgCat category, TextColor subjectColor, String subject, Component details) {
        return send(initiator, category, subjectColor, subject, details, null, 1.0);
    }

    public SentMessage send(@Nullable Player initiator, MsgCat category, TextColor subjectColor, String subject, Component details, SoundEvent sound, Pitch pitch) {
        return send(initiator, category, subjectColor, subject, details, sound, Pitch.base(1.0), 1.0);
    }

    public SentMessage send(@Nullable Player initiator, MsgCat category, TextColor subjectColor, String subject, Component details, SoundEvent sound, double pitch) {
        return send(initiator, category, subjectColor, subject, details, sound, Pitch.base(1.0), 1.0);
    }

    public SentMessage send(@Nullable Player initiator, MsgCat category, TextColor subjectColor, String subject, Component details, SoundEvent sound, Pitch pitch, double volume) {
        return send(initiator, category, subjectColor, subject, details, sound, pitch, volume, Collections.emptySet());
    }

    public SentMessage send(@Nullable Player initiator, MsgCat category, TextColor subjectColor, String subject, Component details, SoundEvent sound, Pitch pitch, double volume, Set<UUID> excludedRecipients) {
        float computedPitch = pitch.compute();
        int buildersOnline = CreativeMessager.countOnlinePlayersWhoCanBuild(instance);

        String initiatorName;
        String bracketHex;
        if (initiator instanceof KloonPlayer kInitiator) {
            initiatorName = kInitiator.getColoredMM();
            bracketHex = kInitiator.getMoniker().ranks().getBestRankLooks().nameColorHex();
        } else {
            initiatorName = initiator == null ? "Unknown" : initiator.getUsername();
            bracketHex = NamedTextColor.DARK_GRAY.asHexString();
        }

        instance.streamPlayers().forEach(recipient -> {
            if (excludedRecipients.contains(recipient.getUuid())) return;

            boolean isSender = recipient == initiator;
            MessagingStorage recipientMessaging = recipient.getCreativeStorage().getMessaging();
            MessagingState state = isSender ? recipientMessaging.getSelf() : recipientMessaging.getOthers();

            Component prefix = !isSender || buildersOnline > 1 ? MM."\{initiatorName}<\{bracketHex}>>" : null;
            Component pitMsg = CreativeMessager.createPitPrefixed(prefix, subjectColor, subject, details);

            if (sound != null) {
                recipient.playSound(sound, computedPitch, volume);
            }

            if (state == MessagingState.CHAT || category.isAlwaysSentToChat()) {
                recipient.sendMessage(pitMsg);
            } else if (state == MessagingState.ACTION_BAR) {
                recipient.getActionBar().queue(pitMsg);
            }
        });
        return new SentMessage(subjectColor, subject, details, null, Pitch.base(1.0), 1.0);
    }
}
