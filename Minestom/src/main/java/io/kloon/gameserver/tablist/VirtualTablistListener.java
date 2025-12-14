package io.kloon.gameserver.tablist;

import io.kloon.gameserver.minestom.events.EventHandler;
import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

import static net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket.*;

public class VirtualTablistListener {
    private static final Logger LOG = LoggerFactory.getLogger(VirtualTablistListener.class);

    private final KloonPlayer player;
    private final VirtualTablist tablist;

    public VirtualTablistListener(KloonPlayer player, VirtualTablist tablist) {
        this.player = player;
        this.tablist = tablist;
    }

    @EventHandler
    public void onTablistPacket(PlayerPacketOutEvent event) {
        ServerPacket packet = event.getPacket();
        if (packet instanceof PlayerInfoUpdatePacket infoPacket) {
            EnumSet<Action> actions = infoPacket.actions();
            if (actions.contains(Action.ADD_PLAYER) && actions.contains(Action.UPDATE_LISTED)) {
                infoPacket.entries().forEach(entry -> {
                    tablist.put(entry, false);
                });
            }
        } else if (packet instanceof PlayerInfoRemovePacket removePacket) {
            removePacket.uuids().forEach(uuid -> tablist.remove(uuid, false));
        }
    }
}
