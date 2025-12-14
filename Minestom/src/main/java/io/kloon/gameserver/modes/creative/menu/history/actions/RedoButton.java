package io.kloon.gameserver.modes.creative.menu.history.actions;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.history.RedoCommand;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class RedoButton implements ChestButton {
    @Override
    public void clickButton(Player player, ButtonClick click) {
        RedoCommand.redo((CreativePlayer) player);
        ChestMenuInv.rerender(player);
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        Component name = MM."<title>/redo";

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.addAll(MM_WRAP."<gray>When you undo a change, you can undo doing that too!");
        lore.add(Component.empty());

        Material icon;
        if (player.getHistory().getFuture().isEmpty()) {
            icon = Material.GRAY_DYE;
            lore.add(MM."<red>Nothing to redo!");
        } else {
            icon = Material.BEEF;
            lore.add(MM."<cta>Click to redo!");
        }

        return MenuStack.of(icon).name(name).lore(lore).build();
    }
}
