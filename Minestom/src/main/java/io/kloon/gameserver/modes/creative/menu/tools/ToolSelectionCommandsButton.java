package io.kloon.gameserver.modes.creative.menu.tools;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ToolSelectionCommandsButton implements ChestButton {
    public static final String ICON = InputFmt.KEYBOARD; // 🖮

    @Override
    public void clickButton(Player player, ButtonClick click) {

    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<#BA00BA>\{ICON} <title>Tools & Commands";

        Lore lore = new Lore();
        lore.add("<dark_gray>Information");
        lore.addEmpty();
        lore.wrap("<gray>Most tools have command shortcuts! Use // commands to immediately edit the world.");
        lore.addEmpty();
        lore.add(MM."<dark_gray>\{ICON} <green>//fill dirt<gray>");
        lore.add("<gray>Fills your <selection> <gray>with dirt");
        lore.addEmpty();
        lore.add(MM."<dark_gray>\{ICON} <green>/fill block dirt");
        lore.wrap(MM."<gray>Adds a <white>Fill Tool <gray>configured with dirt to your inventory.");

        return MenuStack.of(Material.COMMAND_BLOCK, name, lore);
    }
}
