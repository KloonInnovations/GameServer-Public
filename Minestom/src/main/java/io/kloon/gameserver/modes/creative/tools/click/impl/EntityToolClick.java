package io.kloon.gameserver.modes.creative.tools.click.impl;

import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.click.ToolClickSide;
import net.minestom.server.entity.Entity;
import net.minestom.server.item.ItemStack;

public class EntityToolClick extends ToolClick {
    protected final Entity clicked;

    public EntityToolClick(ToolClickSide side, ItemStack item, Entity clicked) {
        super(side, item);
        this.clicked = clicked;
    }

    public Entity getClicked() {
        return clicked;
    }
}
