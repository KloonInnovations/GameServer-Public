package io.kloon.gameserver.modes.creative.menu.patterns.tinker;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PickupTinkerBlockButton implements ChestButton {
    private final TinkerBlockMenu menu;

    public PickupTinkerBlockButton(TinkerBlockMenu menu) {
        this.menu = menu;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        Block block = menu.generateTinkeredBlock();

        player.msg().send(MsgCat.INVENTORY,
                NamedTextColor.GREEN, "PICKED UP!", MM."<gray>Added \{TinkeredBlock.getNameMM(block)} <gray>to your inventory!",
                SoundEvent.ENTITY_ARMADILLO_BRUSH, Pitch.rng(1.5, 0.2));

        ItemStack item = new TinkeredBlock(block).toItem();
        player.getInventoryExtras().grab(item);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Copy Block to Inventory";

        Block block = menu.generateTinkeredBlock();

        Lore lore = new Lore();
        if (block == block.defaultState()) {
            lore.add("<dark_gray>Ye Old Block");
        } else {
            lore.add("<dark_gray>Tinkered");
        }
        lore.addEmpty();
        lore.add(MM."<gray>Block: \{TinkeredBlock.getNameMM(block)}");

        if (block != block.defaultState()) {
            List<String> propertyKeys = block.propertyOptions().keySet().stream().sorted().toList();
            propertyKeys.forEach(key -> {
                String value = block.getProperty(key);
                lore.add(MM."<title>\{key}: <white>\{value}");
            });
        }

        lore.addEmpty();
        lore.add("<cta>Click to pickup!");

        return MenuStack.of(Material.HOPPER_MINECART, name, lore);
    }
}
