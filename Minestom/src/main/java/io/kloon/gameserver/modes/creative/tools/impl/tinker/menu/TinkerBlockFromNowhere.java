package io.kloon.gameserver.modes.creative.tools.impl.tinker.menu;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.minestom.utils.BlockFmt;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.patterns.BlockSelectionMenu;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class TinkerBlockFromNowhere implements ChestButton {
    private final ChestMenu parent;

    public TinkerBlockFromNowhere(ChestMenu parent) {
        this.parent = parent;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        new BlockSelectionMenu(parent, this::pickBlock).display(player);
    }

    private void pickBlock(CreativePlayer player, Block block) {
        player.closeInventory();

        player.msg().send(MsgCat.INVENTORY,
                NamedTextColor.LIGHT_PURPLE, "TADA!", MM."<gray>Created a \{TinkeredBlock.getNameMM(block)}<gray> block!",
                SoundEvent.ENTITY_ARMADILLO_SCUTE_DROP, Pitch.rng(1.3, 0.25));

        ItemStack item = new TinkeredBlock(block).toItem();
        player.getInventoryExtras().grab(item);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Create a Block";

        Lore lore = new Lore();
        lore.wrap("<gray>Select and tinker a block without touching the world.");
        lore.addEmpty();
        lore.add("<cta>Click to create!");

        return MenuStack.of(Material.PINK_GLAZED_TERRACOTTA, name, lore);
    }
}
