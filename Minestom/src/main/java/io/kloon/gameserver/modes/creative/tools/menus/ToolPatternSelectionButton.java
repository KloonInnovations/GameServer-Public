package io.kloon.gameserver.modes.creative.tools.menus;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.patterns.PatternSelectionMenu;
import io.kloon.gameserver.modes.creative.menu.patterns.use.ChoosePatternMenu;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.blocks.PatternBlock;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.data.ItemBoundPattern;
import io.kloon.gameserver.modes.creative.tools.data.ToolData;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.click.Click;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.client.play.ClientClickWindowPacket;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

// need to hook up clickPlayerInventory when using this
public class ToolPatternSelectionButton implements ChestButton {
    private final ChestMenu menu;
    private final int slot;

    protected final CreativeTool<? extends ItemBoundPattern, ?> tool;
    protected final ItemRef itemRef;

    public ToolPatternSelectionButton(CreativeToolMenu<? extends CreativeTool<? extends ItemBoundPattern, ?>> menu, int slot) {
        this(menu, slot, menu.getTool(), menu.getItemRef());
    }

    public ToolPatternSelectionButton(ChestMenu menu, int slot, CreativeTool<? extends ItemBoundPattern, ?> tool, ItemRef itemRef) {
        this.menu = menu;
        this.slot = slot;
        this.tool = tool;
        this.itemRef = itemRef;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        ItemBoundPattern itemBound = tool.getItemBound(itemRef);
        CreativePattern existingPattern = itemBound.getPattern();

        if (click.isRightClick()) {
            if (existingPattern == null || existingPattern instanceof SingleBlockPattern || !existingPattern.hasEditMenu()) {
                new ChoosePatternMenu(menu, this::onPickPattern).display(player);
            } else {
                ChestButton editButton = existingPattern.getType().createEditMenu(menu, existingPattern, this::onPickPattern);
                editButton.clickButton(p, click);
            }
        } else {
            new PatternSelectionMenu(menu, this::onPickPattern)
                    .editing(existingPattern)
                    .display(player);
        }
    }

    private void onPickPattern(CreativePlayer player, CreativePattern pattern) {
        tool.editItemBound(player, itemRef, itemBound -> itemBound.setPattern(pattern));

        menu.reload().display(player);

        if (pattern instanceof SingleBlockPattern single) {
            ToolDataType.ITEM_BOUND.sendMsg(player, MM."<gray>Set fill block to \{TinkeredBlock.getNameMM(single.getBlock())}!");
        } else {
            ToolDataType.ITEM_BOUND.sendMsg(player, MM."<gray>Set fill pattern to \{pattern.labelMM()}!");
        }
        player.playSound(SoundEvent.ENTITY_AXOLOTL_SPLASH, 2, 0.7);
    }

    public void clickPlayerInventory(InventoryPreClickEvent event) {
        event.setCancelled(true);
        if (event.getClick() instanceof Click.HotbarSwap || event.getClick() instanceof Click.OffhandSwap) {
            return;
        }

        clickPlayerInventory(event.getPlayer(), event.getClickedItem());
    }

    public void clickPlayerInventory(Player p, ItemStack clickedItem) {
        CreativePlayer player = (CreativePlayer) p;
        if (clickedItem.material() == Material.AIR) {
            return;
        }

        CreativePattern pattern = getPattern(clickedItem);
        if (pattern == null) {
            player.sendPit(NamedTextColor.RED, "INVALID", MM."<gray>This item doesn't have a corresponding block!");
            player.playSound(SoundEvent.ENTITY_OCELOT_HURT, 1.7, 0.6);
            return;
        }

        boolean edited = tool.editItemBound(player, itemRef, itemBound -> itemBound.setPattern(pattern));
        if (edited) {
            if (pattern instanceof SingleBlockPattern) {
                ToolDataType.ITEM_BOUND.sendMsg(player, MM."<gray>Updated fill block to \{pattern.labelMM()}!");
            } else {
                ToolDataType.ITEM_BOUND.sendMsg(player, MM."<gray>Updated fill pattern to \{pattern.labelMM()}!");
            }
            player.playSound(SoundEvent.ENTITY_AXOLOTL_SPLASH, 2, 0.7);
        }

        ChestMenuInv.rerenderButton(slot, player);
    }

    @Nullable
    private CreativePattern getPattern(ItemStack clickedItem) {
        TinkeredBlock tinkered = TinkeredBlock.get(clickedItem);
        if (tinkered != null) {
            return new SingleBlockPattern(tinkered.block());
        }

        PatternBlock patternBlock = PatternBlock.get(clickedItem);
        if (patternBlock != null) {
            return patternBlock.pattern();
        }

        Block block = Block.fromKey(clickedItem.material().name());
        if (block == null || block.isAir()) {
            return null;
        }
        return new SingleBlockPattern(block);
    }

    @Override
    public ItemStack renderButton(Player player) {
        ItemBoundPattern itemBound = tool.getItemBound(itemRef.getItem());
        CreativePattern pattern = itemBound.getPattern();

        Component title = MM."<title>What to Fill With";

        Lore lore = new Lore();
        lore.add(ToolDataType.ITEM_BOUND.lore());

        if (pattern == null) {
            lore.wrap("<gray>Which block to use for this tool.");
        } else {
            lore.add(MM."<gray>Fill \{pattern.getType().getPropertyName()}");
            lore.add(pattern.lore());
        }
        lore.addEmpty();
        lore.add(getCallToAction(pattern));

        return MenuStack.of(Material.BUCKET, title, lore);
    }

    protected Lore getCallToAction(CreativePattern pattern) {
        Lore lore = new Lore();
        if (pattern instanceof SingleBlockPattern || pattern == null || !pattern.hasEditMenu()) {
            lore.add("<rcta>Click to pick a pattern!");
        } else {
            lore.add("<rcta>Click to edit pattern!");
        }
        lore.add("<lcta>Click to pick from menu!");
        return lore;
    }
}
