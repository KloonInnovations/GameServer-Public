package io.kloon.gameserver.creative.menu.manage.oldsaves.bruh;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.creative.menu.manage.oldsaves.OldSaveButton;
import io.kloon.gameserver.creative.menu.manage.oldsaves.OldWorldSave;
import io.kloon.gameserver.creative.storage.saves.WorldSave;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ManageOldSaveMenu extends ChestMenu {
    private final ChestMenu parent;
    private final OldWorldSave oldSave;

    private final WorldSave save;

    public ManageOldSaveMenu(ChestMenu parent, OldWorldSave oldSave) {
        super("Manage Save", ChestSize.FOUR);
        this.parent = parent;
        this.oldSave = oldSave;

        this.save = oldSave.save();

        setTitleFunction(p -> MM."\{save.cuteName()}");
    }

    @Override
    protected void registerButtons() {
        reg(11, new OldSaveButton(parent, oldSave).withCanManage(false));
        reg(15, slot -> new CopySaveToWorldButton(this, slot, oldSave.world(), save));

        reg().goBack(parent);
    }
}
