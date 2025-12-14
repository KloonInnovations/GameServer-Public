package io.kloon.gameserver.modes.creative.masks.menu.material;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.armor.ArmorFamily;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.masks.MaskItem;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.modes.creative.menu.preferences.colorpicker.ColorPickerMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.color.Color;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class MaskItemColorButton implements ChestButton {
    private final EditMaskItemMenu menu;

    public MaskItemColorButton(EditMaskItemMenu menu) {
        this.menu = menu;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        if (!canEditColor(menu.getMaskItem())) {
            return;
        }

        new ColorPickerMenu(menu, "Armor Piece Color", (_, rgb) -> {
            MaskItem edited = menu.getMaskItem().withArmorColor(new Color(rgb));
            menu.updateMaskAndDisplay(player, edited);

            player.playSound(SoundEvent.ENTITY_GENERIC_SPLASH, Pitch.rng(1.6, 0.2));
            player.sendPit(TextColor.color(rgb), "MASK COLOR!", MM."<gray>Updated armor piece color!");
        }).display(player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        MaskItem maskItem = menu.getMaskItem();
        if (!canEditColor(maskItem)) {
            return ItemStack.AIR;
        }

        Component name = MM."<title>Armor Color";

        Lore lore = new Lore();
        lore.add("<dark_gray>Mask Item");
        lore.addEmpty();
        lore.wrap("<gray>Change the armor piece's color to match your personal style and/or popular fashion.");
        lore.addEmpty();
        lore.add("<cta>Click to pick color!");

        return MenuStack.of(Material.MUSIC_DISC_PRECIPICE, name, lore);
    }

    private static boolean canEditColor(MaskItem maskItem) {
        ArmorFamily armorFamily = ArmorFamily.get(maskItem.getMaterial());
        return armorFamily == ArmorFamily.LEATHER;
    }
}
