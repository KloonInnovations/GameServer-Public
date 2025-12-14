package io.kloon.gameserver.chestmenus;

import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

public interface ChestButton {
    void clickButton(Player player, ButtonClick click);

    ItemStack renderButton(Player player);
}
