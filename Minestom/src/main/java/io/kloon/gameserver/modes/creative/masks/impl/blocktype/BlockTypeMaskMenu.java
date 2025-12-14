package io.kloon.gameserver.modes.creative.masks.impl.blocktype;

import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.EditMaskMenu;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.toggle.NegateMaskToggleButton;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.block.PickMaskBlockTypeButton;
import net.minestom.server.event.inventory.InventoryPreClickEvent;

public class BlockTypeMaskMenu extends EditMaskMenu<BlockTypeMask.Data> {
    private final PickMaskBlockTypeButton pickBlockButton;

    public BlockTypeMaskMenu(EditMaskItemMenu parent, MaskWithData<BlockTypeMask.Data> mask) {
        super(parent, mask);

        this.pickBlockButton = new PickMaskBlockTypeButton(parent, this, false);
    }

    public BlockTypeMask.Data getData() {
        return mask.data();
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
