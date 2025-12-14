package io.kloon.gameserver.chestmenus.builtin;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CancelButton implements ChestButton {
    private final ChestMenu parent;

    public CancelButton(@Nullable ChestMenu parent) {
        this.parent = parent;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        if (parent == null) {
            player.closeInventory();
            return;
        }

        parent.reload().display(player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        ItemBuilder2 builder = MenuStack.of(Material.ARROW)
                .name(MM."<red>Cancel");

        if (parent == null) {
            builder.lore(MM."<gray>and close menu.");
        } else {
            Component lore = MM."<gray>\uD83D\uDD19 "
                    .append(parent.generateTitle(player).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            builder.lore(lore);
        }

        return builder.build();
    }
}
