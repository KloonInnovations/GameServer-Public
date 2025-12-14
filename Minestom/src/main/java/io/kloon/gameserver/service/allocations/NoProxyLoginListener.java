package io.kloon.gameserver.service.allocations;

import io.kloon.gameserver.KloonGameServer;
import io.kloon.gameserver.creative.storage.CreativeWorldsRepo;
import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.owner.player.PlayerWorldOwner;
import io.kloon.gameserver.minestom.events.EventHandler;
import io.kloon.gameserver.modes.GameServerMode;
import io.kloon.gameserver.modes.creative.CreativeMode;
import io.kloon.gameserver.modes.impl.DevMode;
import io.kloon.infra.mongo.accounts.AccountsRepo;
import io.kloon.infra.mongo.accounts.KloonAccount;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class NoProxyLoginListener {
    private static final Logger LOG = LoggerFactory.getLogger(NoProxyLoginListener.class);

    private final KloonGameServer kgs;

    public NoProxyLoginListener(KloonGameServer kgs) {
        this.kgs = kgs;
    }

    @EventHandler
    public void onLogin(AsyncPlayerConfigurationEvent event) {
        GameServerMode mode = kgs.getMode();
        if (mode instanceof CreativeMode creative) {
            handleCreative(creative, event);
        } else if (mode instanceof DevMode devMode) {
            handleDev(devMode, event);
        } else {
            LOG.warn("NoProxyLoginListener cannot handle this mode, fix it if you feel");
        }
    }

    private void handleCreative(CreativeMode creative, AsyncPlayerConfigurationEvent event) {
        Player player = event.getPlayer();

        try {
            AccountsRepo accounts = new AccountsRepo(kgs.getInfra().mongo());
            KloonAccount account = accounts.get(player.getUuid()).get(500, TimeUnit.MILLISECONDS);
            PlayerWorldOwner worldOwner = new PlayerWorldOwner(account);

            CreativeWorldsRepo worlds = new CreativeWorldsRepo(kgs.getInfra());
            List<WorldDef> worldDefs = worlds.defs().getWorldsByOwner(worldOwner).get(500, TimeUnit.MILLISECONDS);
            if (worldDefs.isEmpty()) {
                LOG.warn("Can't jump into creative without any world defs!");
                return;
            }
            WorldDef randomWorld = worldDefs.getFirst();
            Instance instance = creative.createInstanceLatestSave(randomWorld._id()).get(2, TimeUnit.SECONDS);
            event.setSpawningInstance(instance);
            LOG.info("Created an instance from NoProxyLoginListener");
        } catch (Throwable t) {
            LOG.error("Error in NoProxyLoginListener", t);
        }
    }

    private void handleDev(DevMode devMode, AsyncPlayerConfigurationEvent event) {
        event.setSpawningInstance(devMode.getInstance());
    }
}
