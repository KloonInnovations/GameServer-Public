package io.kloon.gameserver.chestmenus;

import io.kloon.gameserver.Kgs;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.inventory.click.ClickType;

public record ButtonClick(
        Player player,
        Click click
) {
    public boolean isRightClick() {
        return click instanceof Click.Right;
    }

    public boolean isSneakClick() {
        return click instanceof Click.LeftShift || click instanceof Click.RightShift;
        //return clickType() == ClickType.SHIFT_CLICK || clickType() == ClickType.START_SHIFT_CLICK;
    }
}
