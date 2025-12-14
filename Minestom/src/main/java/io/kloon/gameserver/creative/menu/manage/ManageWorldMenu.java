package io.kloon.gameserver.creative.menu.manage;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.creative.CreativeWorldsMenu;
import io.kloon.gameserver.creative.menu.manage.oldsaves.WorldSavesMenuProxy;
import io.kloon.gameserver.creative.storage.defs.WorldDef;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ManageWorldMenu extends ChestMenu {
    private final CreativeWorldsMenu parent;
    private final WorldDef world;

    public ManageWorldMenu(CreativeWorldsMenu parent, WorldDef world) {
        super("Manage World", ChestSize.FOUR);
        this.parent = parent;
        this.world = world;
        setTitleFunction(p -> MM."Manage World: \{world.name()}");
    }

    @Override
    protected void registerButtons() {
        reg(10, slot -> new WorldSavesMenuProxy(this, slot, world));
        reg(12, new EditNameManageButton(this, world));
        reg(14, new IconSelectionMenu(parent, this, world));
        reg(16, new DeleteWorldButton(parent.getParent(), world));

        reg().goBack(parent);
    }
}
