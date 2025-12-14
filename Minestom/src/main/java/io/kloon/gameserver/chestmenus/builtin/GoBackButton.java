package io.kloon.gameserver.chestmenus.builtin;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class GoBackButton implements ChestButton {
    private final ChestMenu parent;

    private boolean reloadOnClick = false;

    public GoBackButton(@Nullable ChestMenu parent) {
        this.parent = parent;
    }

    public GoBackButton withReloadOnClick(boolean reloadOnClick) {
        this.reloadOnClick = reloadOnClick;
        return this;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        if (parent == null) {
            return;
        }

        if (reloadOnClick) {
            parent.reload();
        }

        parent.display(player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        if (parent == null) {
            return ItemStack.AIR;
        }

        Component name = MM."<title>\uD83D\uDD19 "
                .append(parent.generateTitle(player).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));

        return MenuStack.of(Material.ARROW).name(name).build();
    }
}
