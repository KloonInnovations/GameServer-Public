package io.kloon.gameserver.modes.creative.tools.click;

import net.minestom.server.item.ItemStack;

public abstract class ToolClick {
    protected final ToolClickSide side;
    protected final ItemStack item;

    public ToolClick(ToolClickSide side, ItemStack item) {
        this.side = side;
        this.item = item;
    }

    public boolean isLeftClick() {
        return side == ToolClickSide.LEFT;
    }

    public boolean isRightClick() {
        return side == ToolClickSide.RIGHT;
    }

    public ToolClickSide side() {
        return side;
    }

    public ItemStack getItem() {
        return item;
    }
}
