package io.kloon.gameserver.modes.hub.hubslist;

import io.kloon.bigbackend.games.hub.HubEntry;
import io.kloon.bigbackend.games.hub.HubsList;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.autoupdate.AutoUpdateMenu;
import io.kloon.gameserver.chestmenus.builtin.GoBackButton;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Supplier;

public class HubsListMenu extends ChestMenu implements AutoUpdateMenu {
    private static final Logger LOG = LoggerFactory.getLogger(HubsListMenu.class);

    private final ChestMenu parent;
    private final Supplier<HubsList> hubsSupplier;

    public HubsListMenu(ChestMenu parent, Supplier<HubsList> hubsSupplier) {
        super("Hub Instance Selection");
        this.parent = parent;
        this.hubsSupplier = hubsSupplier;
    }

    @Override
    protected void registerButtons() {
        List<HubEntry> entries = hubsSupplier.get().entries();
        ChestLayouts.INSIDE.distribute(entries, (slot, entry) -> {
            int index = entries.indexOf(entry);
            reg(slot, new HubListButton(slot, index, entry));
        });

        reg(size.bottomCenter(), new GoBackButton(parent));
    }
}
