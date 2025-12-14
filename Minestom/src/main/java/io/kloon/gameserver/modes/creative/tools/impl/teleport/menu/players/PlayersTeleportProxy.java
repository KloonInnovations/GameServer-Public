package io.kloon.gameserver.modes.creative.tools.impl.teleport.menu.players;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.impl.teleport.commands.TeleportCommand;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PlayersTeleportProxy implements ChestButton {
    private final ChestMenu parent;

    public PlayersTeleportProxy(ChestMenu parent) {
        this.parent = parent;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        if (PlayersTeleportMenu.getTargetPlayers(player).isEmpty()) {
            return;
        }

        new PlayersTeleportMenu(parent, player).display(player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Teleport to Players";

        Lore lore = new Lore();
        lore.wrap("<gray>Go to another player in this world.");
        lore.addEmpty();
        lore.wrap(MM."<gray>Or <dark_gray>\{InputFmt.KEYBOARD} <green>/\{TeleportCommand.LABEL} <username><gray>!");
        lore.addEmpty();

        List<Player> targets = PlayersTeleportMenu.getTargetPlayers(player);
        if (targets.isEmpty()) {
            lore.add("<!cta>No other player in world!");
        } else {
            lore.add("<cta>Click to view players!");
        }

        return MenuStack.of(Material.MANGROVE_DOOR, name, lore);
    }
}
