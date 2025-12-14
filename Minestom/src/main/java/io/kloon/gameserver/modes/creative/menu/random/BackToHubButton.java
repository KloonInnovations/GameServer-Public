package io.kloon.gameserver.modes.creative.menu.random;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.ChestButtonCooldown;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.commands.BackToHubCommand;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class BackToHubButton implements ChestButton {
    private final ChestButtonCooldown throttler = new ChestButtonCooldown();

    @Override
    public void clickButton(Player player, ButtonClick click) {
        if (!throttler.check(player)) {
            return;
        }

        KloonPlayer kp = (KloonPlayer) player;
        BackToHubCommand.sendToHub(kp);
    }

    @Override
    public ItemStack renderButton(Player player) {
        List<Component> lore = new ArrayList<>();
        lore.add(MM."<dark_gray>/hub");
        lore.add(Component.empty());
        lore.addAll(MM_WRAP."<gray>It's very highly possible you've seen everything the creative mode has to offer so far.");
        lore.add(Component.empty());
        lore.add(MM."<cta>Click to go back to hub!");

        return MenuStack.of(Material.BAMBOO_DOOR).name(MM."<title>Back to Hub!").lore(lore).build();
    }
}
