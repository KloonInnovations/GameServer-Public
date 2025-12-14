package io.kloon.gameserver.modes.creative.menu.patterns;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.commands.TinkerCommand;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class SelectionInfoButton implements ChestButton {
    @Override
    public void clickButton(Player player, ButtonClick click) {

    }

    @Override
    public ItemStack renderButton(Player player) {
        List<Component> lore = new ArrayList<>();
        lore.add(MM."<dark_gray>Block Selection");
        lore.addAll(MM_WRAP."<gray>You can <cta>click <gray>a block in your inventory to select it!");
        lore.add(Component.empty());
        lore.addAll(MM_WRAP."<yellow>Tip! <gray>Use <green>/\{TinkerCommand.LABEL} <block> <gray>to quickly pickup a block!");

        return MenuStack.of(Material.BOOK)
                .name(MM."<aqua>ℹ <title>Info")
                .lore(lore)
                .build();
    }
}
