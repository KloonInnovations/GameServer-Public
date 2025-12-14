package io.kloon.gameserver.modes.creative.ux.messaging;

import io.kloon.gameserver.backend.ChatSync;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CreativeMessager {
    private final CreativePlayer player;

    public CreativeMessager(CreativePlayer player) {
        this.player = player;
    }

    public SentMessage send(MsgCat category, TextColor subjectColor, String subject, Component details) {
        return send(category, subjectColor, subject, details, null, 1.0, 1.0);
    }

    public SentMessage send(MsgCat category, TextColor subjectColor, String subject, Component details, SoundEvent sound, double pitch) {
        return send(category, subjectColor, subject, details, sound, pitch, 1.0);
    }

    public SentMessage send(MsgCat category, TextColor subjectColor, String subject, Component details, SoundEvent sound, Pitch pitch) {
        return send(category, subjectColor, subject, details, sound, pitch, 1.0);
    }

    public SentMessage send(MsgCat category, TextColor subjectColor, String subject, Component details, SoundEvent sound, double pitch, double volume) {
        return send(category, subjectColor, subject, details, sound, Pitch.base(pitch), volume);
    }

    public SentMessage send(MsgCat category, TextColor subjectColor, String subject, Component details, SoundEvent sound, Pitch pitch, double volume) {
        int buildersOnline = countOnlinePlayersWhoCanBuild(player.getInstance());

        String initiatorName = player.getDisplayMM();
        String chatHex = ChatSync.getChatHex(player.getAccount().moniker());

        Component prefix = buildersOnline > 1 ? MM."\{initiatorName}<\{chatHex}>>" : null;
        Component pitMsg = createPitPrefixed(prefix, subjectColor, subject, details);

        MessagingState state = player.getCreativeStorage().getMessaging().getSelf();

        if (sound != null) {
            player.playSound(sound, pitch, volume);
        }

        if (state == MessagingState.CHAT || category.isAlwaysSentToChat()) {
            player.sendMessage(pitMsg);
        } else if (state == MessagingState.ACTION_BAR) {
            player.getActionBar().queue(pitMsg);
        }

        return new SentMessage(subjectColor, subject, details, sound, pitch, volume);
    }

    public static int countOnlinePlayersWhoCanBuild(CreativeInstance instance) {
        return (int) instance.streamPlayers()
                .filter(CreativePlayer::canEditWorld)
                .count();
    }

    public static Component createPitPrefixed(@Nullable Component prefix, TextColor color, String subject, Component details) {
        String hexColor = color.asHexString();

        Component msg = Component.empty();

        String subjectFmt = subject.toUpperCase();
        if (!subjectFmt.endsWith("!")) {
            subjectFmt = subjectFmt + "!";
        }

        msg = msg.append(MM."<b><\{hexColor}>\{subjectFmt}</\{hexColor}></b> ");
        if (prefix != null) {
            msg = msg.append(prefix).appendSpace();
        }
        msg = msg.append(details);

        return msg;
    }
}
