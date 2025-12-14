package io.kloon.gameserver.modes.creative.menu.history;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.history.History;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class HistoryInfoButton implements ChestButton {
    @Override
    public void clickButton(Player player, ButtonClick click) {

    }

    @Override
    public ItemStack renderButton(Player player) {
        List<Component> lore = new ArrayList<>();
        lore.add(MM."<dark_gray>Change History");
        lore.addAll(MM_WRAP."<gray>You may have up to <aqua>\{History.RECORDS_LIMIT} <gray>undo records and <aqua>\{History.RECORDS_LIMIT} <gray>redo records in your history.");

        return MenuStack.of(Material.BOOK)
                .name(MM."<aqua>ℹ <title>Info")
                .lore(lore)
                .build();
    }
}
