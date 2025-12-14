package io.kloon.gameserver.modes.creative.masks.menu.editmask.block;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.utils.BlockFmt;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.EditMaskMenu;
import io.kloon.gameserver.modes.creative.menu.patterns.BlockSelectionMenu;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PickMaskBlockTypeButton implements ChestButton {
    private final EditMaskItemMenu menu;
    private final EditMaskMenu<? extends MaskWithBlock> parent;
    private final boolean showTinker;

    public PickMaskBlockTypeButton(EditMaskItemMenu menu, EditMaskMenu<? extends MaskWithBlock> parent, boolean showTinker) {
        this.menu = menu;
        this.parent = parent;
        this.showTinker = showTinker;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        new BlockSelectionMenu(parent, this::onPickBlock).display(player);
    }

    public void onPickBlock(CreativePlayer player, Block block) {
        MaskWithData<? extends MaskWithBlock> mask = parent.getMask();
        mask.data().setBlock(block);
        menu.updateMaskAndDisplay(player, mask);

        ToolDataType.MASK_BOUND.sendPit(player, MM."<gray>Set mask block to <white>\{BlockFmt.getName(block)}<gray>!");
        player.playSound(SoundEvent.ENTITY_AXOLOTL_SPLASH, 2, 0.7);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Required Block";

        Lore lore = new Lore();
        lore.add(ToolDataType.MASK_BOUND.getLoreSubtitle());
        lore.addEmpty();
        lore.wrap("<gray>Which block can be edited by your tools while you wear this mask.");
        lore.addEmpty();

        MaskWithBlock data = parent.getData();
        if (showTinker) {
            TinkeredBlock tinkered = new TinkeredBlock(data.getBlock(), true);
            lore.add(MM."<gray>Block: \{tinkered.getNameMM()}");
            lore.add(tinkered.propertiesLore());
        } else {
            lore.add(MM."<gray>Block: <white>\{BlockFmt.getName(data.getBlock())}");
        }

        lore.addEmpty();
        lore.add("<cta>Click to pick from menu!");

        Material material = data.getBlock().registry().material();
        material = material == null ? Material.BUCKET : material;

        return MenuStack.of(material, name, lore);
    }

    public void clickPlayerInventory(InventoryPreClickEvent event) {
        event.setCancelled(true);
        if (event.getClick() instanceof Click.HotbarSwap) {
            return;
        }

        CreativePlayer player = (CreativePlayer) event.getPlayer();
        ItemStack clickedItem = event.getClickedItem();

        Block block = Block.fromKey(clickedItem.material().name());
        if (block == null || block.isAir()) {
            player.sendPit(NamedTextColor.RED, "INVALID", MM."<gray>This item doesn't have a corresponding block!");
            player.playSound(SoundEvent.ENTITY_OCELOT_HURT, 1.7, 0.6);
            return;
        }

        onPickBlock(player, block);
    }
}
