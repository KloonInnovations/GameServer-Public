package io.kloon.gameserver.modes.creative.tools.click.impl;

import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.click.ToolClickSide;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;

public class InventoryToolClick extends ToolClick {
    private final InventoryPreClickEvent event;

    public InventoryToolClick(ToolClickSide side, ItemStack item, InventoryPreClickEvent event) {
        super(side, item);
        this.event = event;
    }

    public InventoryPreClickEvent getEvent() {
        return event;
    }
}
