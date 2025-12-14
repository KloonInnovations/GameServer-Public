package io.kloon.gameserver.modes.creative.menu.masks;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.masks.MaskTypes;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class MaskSelectionCommandsButton implements ChestButton {
    public static final String ICON = InputFmt.KEYBOARD; // 🖮

    @Override
    public void clickButton(Player player, ButtonClick click) {

    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<#BA00BA>\{ICON} <title>Masks & Commands";

        Lore lore = new Lore();
        lore.add("<dark_gray>Information");
        lore.addEmpty();
        lore.wrap("<gray>Masks can be equipped fast using commands. Some have further customization parameters.");
        lore.addEmpty();
        lore.add(MM."<dark_gray>\{ICON} <green>/mask solid");
        lore.wrap(MM."<gray>Equips a \{MaskTypes.SOLID.getNameMM()} <gray>mask item, or adds the mask to your held wearable.");
        lore.addEmpty();
        lore.add(MM."<dark_gray>\{ICON} <green>/mask block dirt");
        lore.wrap(MM."<gray>Equip/add a \{MaskTypes.BLOCK_TYPE.getNameMM()} <gray>mask matching Dirt blocks.");
        lore.addEmpty();
        lore.add(MM."<dark_gray>\{ICON} <green>/mask !selection");
        lore.wrap(MM."<gray>Equip/add a \{MaskTypes.INSIDE_SELECTION.getNameMM()} <gray>mask matching blocks outside your selection.");

        return MenuStack.of(Material.COMMAND_BLOCK, name, lore);
    }
}
