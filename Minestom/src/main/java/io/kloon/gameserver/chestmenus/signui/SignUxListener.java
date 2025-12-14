package io.kloon.gameserver.chestmenus.signui;

import io.kloon.gameserver.chestmenus.ChestMenuPlayer;
import net.minestom.server.entity.Player;
import net.minestom.server.listener.manager.PacketPlayListenerConsumer;
import net.minestom.server.network.packet.client.play.ClientUpdateSignPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class SignUxListener implements PacketPlayListenerConsumer<ClientUpdateSignPacket> {
    private static final Logger LOG = LoggerFactory.getLogger(SignUxListener.class);

    @Override
    public void accept(ClientUpdateSignPacket packet, Player player) {
        if (!(player instanceof ChestMenuPlayer uxPlayer)) {
            LOG.warn(STR."Received \{packet.getClass().getName()} for \{player} and they're not a \{ChestMenuPlayer.class}");
            return;
        }

        SignUX signUX = uxPlayer.getSignUX();
        if (signUX == null) {
            player.sendMessage(MM."<red>You sent an unexpected sign edit packet!");
            return;
        }
        uxPlayer.setSignUX(null);

        signUX.consume(packet);
    }
}
