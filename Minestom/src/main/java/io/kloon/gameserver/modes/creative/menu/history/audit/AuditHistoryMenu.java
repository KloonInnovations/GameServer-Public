package io.kloon.gameserver.modes.creative.menu.history.audit;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.pagination.MenuPagination;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.history.audit.AuditHistory;
import io.kloon.gameserver.modes.creative.history.audit.AuditRecord;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class AuditHistoryMenu extends ChestMenu {
    private final ChestMenu parent;
    private final AuditHistory history;

    private final MenuPagination pagination;

    public AuditHistoryMenu(ChestMenu parent, AuditHistory history) {
        super("Audit History");
        this.parent = parent;
        this.history = history;

        this.pagination = new MenuPagination(this, ChestLayouts.INSIDE);
    }

    @Override
    protected void registerButtons() {
        List<AuditRecord> records = history.getRecords();
        pagination.distribute(records, (slot, record) -> new AuditRecordButton(this, slot, record), this::reg);

        reg().goBack(parent);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Audit History";

        Lore lore = new Lore();
        lore.wrap(MM."<gray>View history of up to <aqua>\{AuditHistory.LIMIT} records <gray>of changes in the world, but you cannot undo/redo it.");
        lore.addEmpty();
        lore.add("<cta>Click to view audit!");

        return MenuStack.of(Material.BOOKSHELF, name, lore);
    }
}
