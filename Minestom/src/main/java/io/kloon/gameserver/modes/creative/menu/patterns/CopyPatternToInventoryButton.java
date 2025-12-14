package io.kloon.gameserver.modes.creative.menu.patterns;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.blocks.PatternBlock;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class CopyPatternToInventoryButton implements ChestButton {
    private final CreativePattern pattern;

    public CopyPatternToInventoryButton(CreativePattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        if (!pattern.canBePickedUp()) {
            return;
        }

        CreativePlayer player = (CreativePlayer) p;

        player.msg().send(MsgCat.INVENTORY,
                NamedTextColor.GREEN, "PICKED UP!", MM."<gray>Added \{pattern.labelMM()} <gray>to your inventory!",
                SoundEvent.BLOCK_BEACON_ACTIVATE, Pitch.rng(1.9, 0.1));

        ItemStack item = new PatternBlock(pattern).toItem();
        player.getInventoryExtras().grab(item);
    }

    @Override
    public ItemStack renderButton(Player player) {
        if (!pattern.canBePickedUp()) {
            return ItemStack.AIR;
        }

        Component name = MM."<title>Copy Pattern to Inventory";

        Lore lore = new Lore();
        lore.add(MM."<dark_gray>\{pattern.getTypeName()}");
        lore.addEmpty();

        lore.add(pattern.lore());
        lore.addEmpty();

        lore.add("<cta>Click to pickup!");

        return MenuStack.of(Material.COMMAND_BLOCK_MINECART, name, lore);
    }
}
