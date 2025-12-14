package io.kloon.gameserver.chestmenus.pagination;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class NextPageButton implements ChestButton {
    private final MenuPagination pagination;

    public NextPageButton(MenuPagination pagination) {
        this.pagination = pagination;
    }

    private int getNextIndex() {
        return pagination.getPageIndex() + 1;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        pagination.setPageIndexAndRefresh(player, getNextIndex());
    }

    @Override
    public ItemStack renderButton(Player player) {
        return MenuStack.of(Material.FLOWER_BANNER_PATTERN)
                .name(MM."<title>Next Page <dark_green>➡")
                .lore(MM."<gray>To (\{getNextIndex() + 1}/\{pagination.getTotalPages()})")
                .build();
    }
}
