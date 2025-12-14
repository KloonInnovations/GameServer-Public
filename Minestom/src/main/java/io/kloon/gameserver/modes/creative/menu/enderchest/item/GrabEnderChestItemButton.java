package io.kloon.gameserver.modes.creative.menu.enderchest.item;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.storage.enderchest.EnderChestItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class GrabEnderChestItemButton implements ChestButton {
    private final EnderChestItem item;

    public GrabEnderChestItemButton(EnderChestItem item) {
        this.item = item;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        copyToInventory(player, item);
    }

    public static void copyToInventory(CreativePlayer player, EnderChestItem item) {
        ItemStack stack = item.getItemStack();
        Component name = item.getName();

        player.playSound(SoundEvent.ENTITY_ARMADILLO_AMBIENT, Pitch.base(1.3).addRand(0.1));
        player.sendPit(NamedTextColor.DARK_PURPLE, "COPIED!", MM."<gray>Picked up ".append(name).append(MM." <gray>from ender chest!"));

        player.getInventoryExtras().grab(stack);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Lore lore = new Lore();
        lore.add("<cta>Click to copy to inventory!");

        return MenuStack.extraLore(item.getItemStack(), lore).build();
    }
}
