package io.kloon.gameserver.modes.creative.menu.enderchest.item;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.CreativeMainMenu;
import io.kloon.gameserver.modes.creative.menu.enderchest.EnderChestMenu;
import io.kloon.gameserver.modes.creative.storage.enderchest.EnderChestItem;
import io.kloon.gameserver.modes.creative.storage.enderchest.EnderChestStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class DeleteEnderChestItemButton implements ChestButton {
    private final EnderChestItem item;

    public DeleteEnderChestItemButton(EnderChestItem item) {
        this.item = item;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        Component name = item.getName();
        player.playSound(SoundEvent.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.9f, 0.35f);
        player.sendPit(NamedTextColor.RED, "DELETED!", MM."<gray>Deleted ".append(name).append(MM."<gray> from your ender chest!"));

        EnderChestStorage echest = player.getEnderChest();
        if (!echest.checkActionCooldown()) {
            return;
        }

        echest.delete(item);

        CreativeMainMenu mainMenu = new CreativeMainMenu(player);
        new EnderChestMenu(mainMenu, player).display(player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<red>Delete Item";

        Lore lore = new Lore();
        lore.wrap("<gray>Permanently erase this item from your ender chest.");
        lore.addEmpty();
        lore.add(MM."<cta>Click to delete!");

        return MenuStack.of(Material.TNT, name, lore);
    }
}
