package io.kloon.gameserver.chestmenus.builtin;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ConfirmPaddingButton implements ChestButton {
    private static final ItemStack ITEM = MenuStack.of(Material.LIME_STAINED_GLASS_PANE).name(MM."").glowing().build();

    @Override
    public void clickButton(Player player, ButtonClick click) {

    }

    @Override
    public ItemStack renderButton(Player player) {
        return ITEM;
    }
}
