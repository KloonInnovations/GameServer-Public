package io.kloon.gameserver.player.settings.menu;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.commands.player.SettingsCommand;
import io.kloon.gameserver.player.settings.PlayerSettingsStorage;
import io.kloon.gameserver.player.settings.SettingsToggle;
import io.kloon.gameserver.player.settings.menu.block.BlockProxyButton;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class GeneralSettingsMenu extends ChestMenu {
    private final ChestMenu parent;

    public static final SettingsToggle ACCEPT_JOINS = new SettingsToggle(
            true, Material.AXOLOTL_BUCKET, "\uD83D\uDC65", "Accept /join",
            new Lore().wrap("<gray>Whether you accept that players use <green>/join <gray>on you, letting them transfer to your instance."),
            PlayerSettingsStorage::isAcceptingJoins,
            PlayerSettingsStorage::setAcceptJoins);

    public GeneralSettingsMenu(ChestMenu parent) {
        super("General Settings", ChestSize.FOUR);
        this.parent = parent;
    }

    @Override
    protected void registerButtons() {
        reg(11, ACCEPT_JOINS::toButton);

        reg(15, new BlockProxyButton(this));

        reg().goBack(parent);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>General Settings";

        Lore lore = new Lore();
        lore.add(MM."<cmd>\{SettingsCommand.LABEL}");
        lore.addEmpty();
        lore.wrap("<gray>Edit your personal preferences.");
        lore.wrap("<#FF5C92>Relevant across the server!");
        lore.addEmpty();
        lore.add(MM."<cta>Click to configure!");

        return MenuStack.of(Material.LOOM, name, lore);
    }
}
