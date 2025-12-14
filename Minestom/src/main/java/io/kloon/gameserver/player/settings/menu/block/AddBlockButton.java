package io.kloon.gameserver.player.settings.menu.block;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.commands.player.block.BlockCommand;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class AddBlockButton implements ChestButton {
    private final BlockProxyButton blockProxy;

    public AddBlockButton(BlockProxyButton blockProxy) {
        this.blockProxy = blockProxy;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        KloonPlayer player = (KloonPlayer) p;

        String[] lines = SignUX.inputLines("Enter target", "username");
        SignUX.display(player, lines, input -> {
            String usernameInput = input[0];
            BlockCommand.block(player, usernameInput).thenRunAsync(() -> {
                blockProxy.fetchAndHandleClick(player);
            }, player.scheduler());
        });
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Block a Player";

        Lore lore = new Lore();
        lore.add(MM."<cmd>\{BlockCommand.LABEL} <username>");
        lore.addEmpty();
        lore.wrap("<gray>Blocking a player prevents them from interacting and chatting with you, and vice versa.");
        lore.addEmpty();
        lore.add("<cta>Click to enter username!");

        return MenuStack.of(Material.CRIMSON_SIGN, name, lore);
    }
}
