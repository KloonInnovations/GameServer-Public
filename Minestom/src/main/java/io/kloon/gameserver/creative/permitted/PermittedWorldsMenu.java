package io.kloon.gameserver.creative.permitted;

import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.pagination.MenuPagination;
import io.kloon.gameserver.creative.CreativeWorldsMenu;
import io.kloon.gameserver.creative.menu.CreativeWorldButton;
import io.kloon.gameserver.creative.storage.defs.WorldDef;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PermittedWorldsMenu extends ChestMenu {
    private final CreativeWorldsMenu parent;
    private final List<WorldDef> worldDefs;

    private final MenuPagination pagination;

    public PermittedWorldsMenu(CreativeWorldsMenu parent, List<WorldDef> worldDefs) {
        super("Worlds with Build Permits");
        this.parent = parent;

        this.pagination = new MenuPagination(this, ChestLayouts.INSIDE);
        this.worldDefs = new ArrayList<>(worldDefs);

        setTitleFunction(_ -> pagination.titleWithPages(MM."Worlds with Build Permits"));
    }

    @Override
    protected void registerButtons() {
        pagination.distribute(worldDefs, (slot, worldDef) -> new CreativeWorldButton(this, slot, worldDef), this::reg);
        reg().goBack(parent);
    }
}
