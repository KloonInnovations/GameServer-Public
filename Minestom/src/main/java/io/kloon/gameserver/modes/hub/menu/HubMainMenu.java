package io.kloon.gameserver.modes.hub.menu;

import io.kloon.bigbackend.games.hub.HubsList;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.autoupdate.NatsSubCache;
import io.kloon.gameserver.creative.CreativeWorldsMenuProxy;
import io.kloon.gameserver.modes.hub.hubslist.HubListMenuProxy;
import io.kloon.gameserver.player.settings.menu.GeneralSettingsMenu;

public class HubMainMenu extends ChestMenu {
    private final NatsSubCache<HubsList> hubsCache;

    public HubMainMenu(NatsSubCache<HubsList> hubsCache) {
        super("Main Menu", ChestSize.THREE);
        this.hubsCache = hubsCache;
    }

    @Override
    protected void registerButtons() {
        reg(11, new CreativeWorldsMenuProxy(this, Kgs.INSTANCE.getWorldListsCache()));

        reg(15, new HubListMenuProxy(this, hubsCache));

        reg(size.last(), new GeneralSettingsMenu(this));
    }
}
