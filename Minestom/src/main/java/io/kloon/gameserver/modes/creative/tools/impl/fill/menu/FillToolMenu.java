package io.kloon.gameserver.modes.creative.tools.impl.fill.menu;

import io.kloon.gameserver.modes.creative.tools.impl.fill.FillTool;
import io.kloon.gameserver.modes.creative.tools.menus.CreativeToolMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import net.minestom.server.event.inventory.InventoryPreClickEvent;

public class FillToolMenu extends CreativeToolMenu<FillTool> {
    private final FillSelectMaterialButton selectMaterial;
    private static final int FILL_SELECT_SLOT = 21;

    public FillToolMenu(FillTool tool, ItemRef itemRef) {
        super(tool, itemRef);

        this.selectMaterial = new FillSelectMaterialButton(FILL_SELECT_SLOT, this);

        setBreadcrumbs(tool.getType().getMaterial(), tool.getType().getDisplayName(), "Configuring tool...");
    }

    public FillTool getTool() {
        return tool;
    }

    public ItemRef getItemRef() {
        return itemRef;
    }

    public FillSelectMaterialButton getSelectMaterialButton() {
        return selectMaterial;
    }

    @Override
    protected void registerButtons() {
        reg(FILL_SELECT_SLOT, selectMaterial);
        reg(23, new FillSelectAirButton(this));

        reg().toolCommands(this);
    }

    @Override
    public void handleClickPlayerInventoryWhileOpen(InventoryPreClickEvent event) {
        selectMaterial.clickPlayerInventory(event);
    }
}
