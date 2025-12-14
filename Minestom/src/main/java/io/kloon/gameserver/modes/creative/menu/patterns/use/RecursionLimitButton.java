package io.kloon.gameserver.modes.creative.menu.patterns.use;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class RecursionLimitButton implements ChestButton {
    @Override
    public void clickButton(Player player, ButtonClick click) {

    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<red>Can't Use Block Pattern";

        Lore lore = new Lore();
        lore.wrap("<gray>Too many patterns inside other patterns!");
        lore.addEmpty();
        lore.add("<!cta>Pattern recursion limit!");

        return MenuStack.of(Material.RED_TERRACOTTA, name, lore);
    }
}
