package io.kloon.gameserver.modes.creative.menu.patterns;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.listing.MenuList;
import io.kloon.gameserver.minestom.utils.BlockFmt;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.patterns.use.ChoosePatternMenu;
import io.kloon.gameserver.modes.creative.menu.patterns.use.RecursionLimitButton;
import io.kloon.gameserver.modes.creative.menu.util.CreativeConsumer;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.RecursivePattern;
import io.kloon.gameserver.modes.creative.patterns.blocks.PatternBlock;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.TinkeredBlock;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PatternSelectionMenu extends ChestMenu {
    private final ChestMenu parent;
    private final CreativeConsumer<CreativePattern> onSelect;

    private @Nullable CreativePattern parentPattern;
    private @Nullable CreativePattern existing;

    private final MenuList<Block> menuList;

    public PatternSelectionMenu(ChestMenu parent, CreativeConsumer<CreativePattern> onSelect) {
        super("Block Selection");
        this.parent = parent;
        this.onSelect = onSelect;

        CreativeConsumer<Block> onSelectBlock = (player, block) -> onSelect.accept(player, new SingleBlockPattern(block));;
        this.menuList = new MenuList<>(this, ChestLayouts.INSIDE, block -> new SelectBlockProxy(this, onSelectBlock, block));
        menuList.withSearch(BlockFmt::getName);

        setBreadcrumbs(parent, "Select Block", "Picking a block/pattern...");
    }

    public PatternSelectionMenu editing(@Nullable CreativePattern existing) {
        this.existing = existing;
        return this;
    }

    public PatternSelectionMenu parent(@Nullable CreativePattern parent) {
        this.parentPattern = parent;
        return this;
    }

    @Override
    protected void registerButtons() {
        List<Block> blocks = BlockSelectionMenu.BLOCK_LIST.get();
        menuList.distribute(blocks, this::reg);

        reg().goBack(parent);
        reg().breadcrumbs();

        reg(size.last() - 1, new SelectionInfoButton());
        reg(size.last() - 7, new SelectAirButton(onSelect).offerPassthrough());

        if (parentPattern instanceof RecursivePattern recursive && recursive.hasReachedRecursionLimit()) {
            reg(size().bottomCenter() - 1, new RecursionLimitButton());
        } else {
            reg(size().bottomCenter() - 1, new ChoosePatternMenu(this, onSelect).editing(existing));
        }
    }

    @Override
    public void handleClickPlayerInventoryWhileOpen(InventoryPreClickEvent event) {
        event.setCancelled(true);
        CreativePlayer player = (CreativePlayer) event.getPlayer();
        CreativePattern pattern = grabPatternFromInventoryClick(event, parentPattern);

        if (pattern != null) {
            onSelect.accept(player, pattern);
        }
    }

    @Nullable
    public static CreativePattern grabPatternFromInventoryClick(InventoryPreClickEvent event, @Nullable CreativePattern parentPattern) {
        CreativePlayer player = (CreativePlayer) event.getPlayer();

        ItemStack item = event.getClickedItem();
        if (item.isAir()) {
            return null;
        }

        PatternBlock patternBlock = PatternBlock.get(item);
        if (patternBlock != null) {
            CreativePattern pattern = patternBlock.pattern().copy();
            if (pattern instanceof RecursivePattern recursive && parentPattern != null) {
                recursive.setParent(parentPattern.copy());
                if (recursive.computeDeepestChildDepth() > RecursivePattern.RECURSION_LIMIT) {
                    player.playSound(SoundEvent.ENTITY_CAT_DEATH, 1.3);
                    player.sendPit(NamedTextColor.RED, "CAN'T!", MM."<gray>Would be too many patterns inside other patterns!");
                    return null;
                }
            }
            return pattern.copy();
        }

        Block block = item.material().block();
        if (block == null) {
            player.playSound(SoundEvent.ENTITY_CAT_DEATH, 1.3);
            player.sendPit(NamedTextColor.RED, "NOT A BLOCK!", MM."<gray>This item can't be placed as a block!");
            return null;
        }

        if (player.getCreative().isTool(item)) {
            player.playSound(SoundEvent.ENTITY_CAT_DEATH, 1.3);
            player.sendPit(NamedTextColor.RED, "WHO IS A TOOL?!", MM."<gray>Can't use a tool as a block!");
            return null;
        }

        TinkeredBlock tinkered = TinkeredBlock.get(item);
        if (tinkered != null) {
            block = tinkered.block();
        }

        return new SingleBlockPattern(block);
    }
}
