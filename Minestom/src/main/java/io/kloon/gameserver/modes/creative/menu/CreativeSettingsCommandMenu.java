package io.kloon.gameserver.modes.creative.menu;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.modes.creative.menu.preferences.CreativePreferencesMenu;
import io.kloon.gameserver.player.settings.menu.GeneralSettingsMenu;

public class CreativeSettingsCommandMenu extends ChestMenu {
    private final ChestMenu parent;

    public CreativeSettingsCommandMenu(ChestMenu parent) {
        super("/settings (Creative)", ChestSize.FOUR);
        this.parent = parent;
    }

    @Override
    protected void registerButtons() {
        reg(11, new CreativePreferencesMenu(this, false));
        reg(15, new GeneralSettingsMenu(this));

        reg().goBack(parent);
    }
}
