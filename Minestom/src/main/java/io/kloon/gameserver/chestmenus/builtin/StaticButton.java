package io.kloon.gameserver.chestmenus.builtin;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

public class StaticButton implements ChestButton {
    private final ItemStack renderedItem;

    public StaticButton(ItemStack renderedItem) {
        this.renderedItem = renderedItem;
    }

    public StaticButton(ItemStack.Builder builder) {
        this.renderedItem = builder.build();
    }

    public StaticButton(ItemBuilder2 builder) {
        this.renderedItem = builder.build();
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {

    }

    @Override
    public ItemStack renderButton(Player player) {
        return renderedItem;
    }
}
