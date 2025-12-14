package io.kloon.gameserver.modes.creative.menu.preferences.messaging;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class MessagingPreferencesProxy implements ChestButton {
    public static final String ICON = "\uD83D\uDCAC"; // 💬

    private final ChestMenu parent;

    public MessagingPreferencesProxy(ChestMenu parent) {
        this.parent = parent;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        new MessagingPreferencesMenu(parent, player).display(player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<yellow>\{ICON} <title>Messaging";

        Lore lore = new Lore();
        lore.add(ToolDataType.PLAYER_BOUND.getLoreSubtitle());
        lore.addEmpty();

        lore.wrap("<gray>Edit where you get messages from tools and how much of them.");
        lore.addEmpty();

        lore.add("<cta>Click to edit!");

        return MenuStack.of(Material.BOOK, name, lore);
    }
}
