package io.kloon.gameserver.modes.creative.menu.enderchest;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.enderchest.EnderChestCommand;
import io.kloon.gameserver.modes.creative.commands.enderchest.EnderChestSaveCommand;
import io.kloon.gameserver.modes.creative.storage.enderchest.EnderChestStorage;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class EnderChestInfoButton implements ChestButton {
    private final CreativePlayer player;

    public EnderChestInfoButton(CreativePlayer player) {
        this.player = player;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {

    }

    @Override
    public ItemStack renderButton(Player p) {
        Component name = MM."<aqua>ℹ <title>Info";

        Lore lore = new Lore();
        lore.add(MM."<dark_gray>Ender Chest");
        lore.addEmpty();

        int have = player.getEnderChest().getItems().size();
        int limit = EnderChestStorage.ITEMS_LIMIT;

        lore.wrap(MM."<gray>You have <light_purple>\{have}<dark_purple>/\{limit} items <gray>that you may store in the ender chest.");
        lore.addEmpty();
        lore.add(MM."<title>Adding Items");
        lore.add(MM."<dark_gray>▪ <cta>Click <gray>in your inventory here.");

        String command = STR."/\{EnderChestCommand.ONE} \{EnderChestSaveCommand.LABEL}";
        lore.add(MM."<dark_gray>▪ <gray>Use <dark_purple>\{command} <gray>to add your held item.");

        return MenuStack.of(Material.BOOK, name, lore);
    }
}
