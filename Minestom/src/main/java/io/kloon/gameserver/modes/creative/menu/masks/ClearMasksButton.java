package io.kloon.gameserver.modes.creative.menu.masks;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.masks.ClearMasksCommand;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ClearMasksButton implements ChestButton {
    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        ClearMasksCommand.clearMasks(player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Clear Masks";

        Lore lore = new Lore();
        lore.add(MM."<cmd>\{ClearMasksCommand.LABEL}");
        lore.add("<can_undo>");
        lore.addEmpty();

        lore.wrap("<gray>Wipe your equipped armor pieces.");
        lore.addEmpty();

        lore.add("<cta>Click to clear!");

        return MenuStack.of(Material.TNT, name, lore);
    }
}
