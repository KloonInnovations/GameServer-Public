package io.kloon.gameserver.player.settings.menu.block;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.pagination.MenuPagination;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.infra.mongo.blocks.PlayerBlock;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class BlocksManageMenu extends ChestMenu {
    private final ChestMenu parent;
    private final BlockProxyButton blockProxy;
    private final List<PlayerBlock> blocks;

    private final MenuPagination pagination;

    public BlocksManageMenu(ChestMenu parent, BlockProxyButton blockProxy, List<PlayerBlock> blocks) {
        super("Player Blocks");
        this.parent = parent;
        this.blockProxy = blockProxy;
        this.blocks = new ArrayList<>(blocks);
        this.pagination = new MenuPagination(this, ChestLayouts.INSIDE);
    }

    @Override
    protected void registerButtons() {
        blocks.sort(Comparator.comparingLong(b -> -b.getTimestamp()));

        if (blocks.isEmpty()) {
            reg(size.middleCenter(), MenuStack.of(Material.PIGLIN_HEAD)
                            .name(MM."<green>Nobody Blocked!")
                            .lore(MM_WRAP."<gray>You haven't blocked anyone.")
                            .buildButton());
        } else {
            pagination.distribute(blocks, (slot, block) -> new BlockedPlayerButton(this, slot, blockProxy, block), this::reg);
        }

        reg(size.bottomCenter() + 1, new AddBlockButton(blockProxy));

        reg().goBack(parent);
    }
}
