package io.kloon.gameserver.modes.creative.buildpermits.menu.manage;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.creative.storage.defs.BuildPermit;
import io.kloon.gameserver.modes.creative.buildpermits.menu.BuildPermitsMenu;
import io.kloon.infra.mongo.accounts.projections.KloonMoniker;

public class ManagePermitMenu extends ChestMenu {
    private final BuildPermitsMenu parent;
    private final BuildPermit permit;
    private final KloonMoniker permitOwner;

    public ManagePermitMenu(BuildPermitsMenu parent, BuildPermit permit, KloonMoniker permitOwner) {
        super("Build Permit", ChestSize.FOUR);
        this.parent = parent;
        this.permit = permit;
        this.permitOwner = permitOwner;
    }

    @Override
    protected void registerButtons() {
        reg(size.middleCenter(), new RevokePermitButton(parent, permit, permitOwner));

        reg().goBack(parent);
    }
}
