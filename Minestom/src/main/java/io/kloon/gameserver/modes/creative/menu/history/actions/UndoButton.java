package io.kloon.gameserver.modes.creative.menu.history.actions;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.history.UndoCommand;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class UndoButton implements ChestButton {
    @Override
    public void clickButton(Player p, ButtonClick click) {
        UndoCommand.undo((CreativePlayer) p);
        ChestMenuInv.rerender(p);
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        Component name = MM."<title>/undo";

        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.addAll(MM_WRAP."<gray>Undo your last change, in case you made a mistake.");
        lore.add(Component.empty());

        Material icon;
        if (player.getHistory().getPast().isEmpty()) {
            icon = Material.GRAY_DYE;
            lore.add(MM."<red>Nothing to undo!");
        } else {
            icon = Material.COOKED_BEEF;
            lore.add(MM."<cta>Click to undo!");
        }

        return MenuStack.of(icon).name(name).lore(lore).build();
    }
}
