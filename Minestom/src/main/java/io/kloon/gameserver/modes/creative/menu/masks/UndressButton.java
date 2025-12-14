package io.kloon.gameserver.modes.creative.menu.masks;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.masks.UndressCommand;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class UndressButton implements ChestButton {
    @Override
    public void clickButton(Player player, ButtonClick click) {
        UndressCommand.undress((CreativePlayer) player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Undress";

        Lore lore = new Lore();
        lore.add(MM."<cmd>\{UndressCommand.LABEL}");
        lore.addEmpty();

        lore.wrap("<gray>Take off all pieces of armor you're wearing.");
        lore.addEmpty();

        lore.add("<cta>Click to undress!");

        return MenuStack.of(Material.TOTEM_OF_UNDYING, name, lore);
    }
}
