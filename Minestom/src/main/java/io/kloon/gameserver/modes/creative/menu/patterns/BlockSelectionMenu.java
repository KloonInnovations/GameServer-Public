package io.kloon.gameserver.modes.creative.menu.patterns;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.listing.MenuList;
import io.kloon.gameserver.minestom.utils.BlockFmt;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.util.CreativeConsumer;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.List;
import java.util.Objects;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class BlockSelectionMenu extends ChestMenu {
    private final ChestMenu parent;
    private final CreativeConsumer<Block> onSelect;

    private final MenuList<Block> menuList;

    public BlockSelectionMenu(ChestMenu parent, CreativeConsumer<Block> onSelect) {
        super("Block Selection");
        this.parent = parent;
        this.onSelect = onSelect;
        this.menuList = new MenuList<>(this, ChestLayouts.INSIDE, block -> new SelectBlockProxy(this, onSelect, block));
        menuList.withSearch(BlockFmt::getName);

        setBreadcrumbs(parent, "Block Selection", "Picking a block...");
    }

    @Override
    protected void registerButtons() {
        List<Block> blocks = BlockSelectionMenu.BLOCK_LIST.get();
        menuList.distribute(blocks, this::reg);

        reg().goBack(parent);
        reg().breadcrumbs();

        reg(size.last() - 1, new SelectionInfoButton());
        reg(size.last() - 7, SelectAirButton.selectBlock(onSelect));
    }

    @Override
    public void handleClickPlayerInventoryWhileOpen(InventoryPreClickEvent event) {
        event.setCancelled(true);

        CreativePlayer player = (CreativePlayer) event.getPlayer();
        CreativePattern pattern = PatternSelectionMenu.grabPatternFromInventoryClick(event, null);
        if (!(pattern instanceof SingleBlockPattern single)) {
            player.playSound(SoundEvent.ENTITY_CAT_DEATH, 1.3);
            player.sendPit(NamedTextColor.RED, "CAN'T!", MM."<gray>Cannot use a pattern here, we need a block.");
            return;
        }

        onSelect.accept(player, single.getBlock());
    }

    public static final Supplier<List<Block>> BLOCK_LIST = Suppliers.memoize(BlockSelectionMenu::generateBlocksList);
    private static List<Block> generateBlocksList() {
        return Material.values().stream()
                .map(Material::block)
                .filter(Objects::nonNull)
                .toList();
    }
}
