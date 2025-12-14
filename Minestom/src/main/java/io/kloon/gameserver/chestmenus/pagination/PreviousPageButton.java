package io.kloon.gameserver.chestmenus.pagination;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PreviousPageButton implements ChestButton {
    private final MenuPagination pagination;

    public PreviousPageButton(MenuPagination pagination) {
        this.pagination = pagination;
    }

    private int getPreviousIndex() {
        return pagination.getPageIndex() - 1;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        pagination.setPageIndexAndRefresh(player, getPreviousIndex());
    }

    @Override
    public ItemStack renderButton(Player player) {
        return MenuStack.of(Material.FLOWER_BANNER_PATTERN)
                .name(MM."<dark_green>⬅ <title>Previous Page")
                .lore(MM."<gray>To (\{getPreviousIndex() + 1}/\{pagination.getTotalPages()})")
                .build();
    }
}
