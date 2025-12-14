package io.kloon.gameserver.ux.actionbar;

import io.kloon.gameserver.minestom.GlobalMinestomTicker;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.Component;

import java.util.LinkedList;
import java.util.Queue;

public class ActionBarQueue {
    private final KloonPlayer player;

    private final Queue<Component> messages = new LinkedList<>();
    private int lastMessageSentTick = 0;

    public static final int QUEUE_LIMIT = 250;

    public ActionBarQueue(KloonPlayer player) {
        this.player = player;
    }

    public void queue(Component message) {
        if (player instanceof CreativePlayer cPlayer) { // eek cast
            if (!cPlayer.getCreativeStorage().getMessaging().isActionBarQueueEnabled()) {
                player.sendActionBar(message);
                return;
            }
        }

        if (messages.size() >= QUEUE_LIMIT) {
            player.sendActionBar(message);
            return;
        }

        messages.add(message);
    }

    public void tick() {
        int currentTick = GlobalMinestomTicker.getTick();
        int sinceLastMsg = currentTick - lastMessageSentTick;
        int betweenMsgs = computeTicksBetweenMessages();
        if (sinceLastMsg < betweenMsgs) {
            return;
        }

        Component msg = messages.poll();
        if (msg == null) {
            return;
        }

        lastMessageSentTick = currentTick;

        player.sendActionBar(msg);
    }

    private int computeTicksBetweenMessages() {
        int count = messages.size();
        if (count <= 3) {
            return 22;
        }
        if (count <= 6) {
            return 19;
        }
        if (count <= 10) {
            return 17;
        }
        if (count <= 15) {
            return 11;
        }
        if (count <= 25) {
            return 6;
        }
        return 1;
    }
}
