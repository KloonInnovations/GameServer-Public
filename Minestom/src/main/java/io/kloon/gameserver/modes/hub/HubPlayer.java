package io.kloon.gameserver.modes.hub;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.modes.hub.sidebar.HubSidebar;
import io.kloon.gameserver.modes.hub.ux.HubHeaderFooter;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.player.proxyinfo.KloonProxyInfo;
import io.kloon.gameserver.ux.headerfooter.KloonHeaderFooter;
import io.kloon.gameserver.ux.sidebar.KloonSidebar;
import io.kloon.infra.mongo.accounts.KloonAccount;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class HubPlayer extends KloonPlayer {
    public HubPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
        super(playerConnection, gameProfile);
    }

    @Override
    protected KloonSidebar createSidebar() {
        return new HubSidebar(this);
    }

    @Override
    protected KloonHeaderFooter createHeaderFooter() {
        return new HubHeaderFooter(this);
    }

    @Override
    public void spawn() {
        super.spawn();

        KloonProxyInfo proxyInfo = getProxyInfo();
        if (proxyInfo != KloonProxyInfo.DEFAULT) {
            KloonAccount account = getAccount();
            account.setPreferredDatacenter(proxyInfo.datacenter());
            Kgs.getAccountsRepo().update(account);
        }
    }
}
