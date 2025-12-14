package io.kloon.gameserver.modes.creative.masks.impl.exactblock;

import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.block.PickMaskBlockTypeButton;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.EditMaskMenu;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.toggle.NegateMaskToggleButton;
import net.minestom.server.event.inventory.InventoryPreClickEvent;

public class ExactBlockMaskMenu extends EditMaskMenu<ExactBlockMask.Data> {
    private final PickMaskBlockTypeButton pickBlockButton;

    public ExactBlockMaskMenu(EditMaskItemMenu parent, MaskWithData<ExactBlockMask.Data> mask) {
        super(parent, mask);
        this.pickBlockButton = new PickMaskBlockTypeButton(parent, this, true);
    }

    @Override
    protected void registerButtons() {
        super.registerButtons();

        reg(12, pickBlockButton);
        reg(14, new NegateMaskToggleButton(parent, mask));
    }

    @Override
    public void handleClickPlayerInventoryWhileOpen(InventoryPreClickEvent event) {
        pickBlockButton.clickPlayerInventory(event);
    }
}
