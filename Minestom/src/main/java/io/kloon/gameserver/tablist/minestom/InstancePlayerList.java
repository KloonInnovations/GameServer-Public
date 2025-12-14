package io.kloon.gameserver.tablist.minestom;

import io.kloon.gameserver.minestom.KloonInstance;
import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.playerlist.PlayerList;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

public class InstancePlayerList implements PlayerList {
    private static final Logger LOG = LoggerFactory.getLogger(InstancePlayerList.class);

    private final KloonPlayer viewer;
    private final KloonInstance instance;

    public InstancePlayerList(KloonPlayer viewer, KloonInstance instance) {
        this.viewer = viewer;
        this.instance = instance;
    }

    @Override
    public Collection<Player> getBroadcastRecipients() {
        return instance.getPlayers();
    }

    @Override
    public void send(@NotNull ServerPacket serverPacket) {
        viewer.sendPacket(serverPacket);
    }

    @Override
    public PlayerInfoUpdatePacket createAddPlayerToList(Player player) {
        return new PlayerInfoUpdatePacket(EnumSet.of(PlayerInfoUpdatePacket.Action.ADD_PLAYER, PlayerInfoUpdatePacket.Action.UPDATE_LISTED, PlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME),
                List.of(toEntry(player)));
    }

    @Override
    public PlayerInfoUpdatePacket.Entry toEntry(Player target) {
        return viewer.getTabList().getOnlineEntry(target);
    }
}
