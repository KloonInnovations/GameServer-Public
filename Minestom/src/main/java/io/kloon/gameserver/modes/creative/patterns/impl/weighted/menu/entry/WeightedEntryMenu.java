package io.kloon.gameserver.modes.creative.patterns.impl.weighted.menu.entry;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.builtin.GoBackButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.patterns.PatternSelectionMenu;
import io.kloon.gameserver.modes.creative.patterns.impl.weighted.menu.WeightedPatternMenu;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.patterns.impl.weighted.WeightedPattern;
import io.kloon.gameserver.util.weighted.EntryInWeightedTable;
import io.kloon.gameserver.util.weighted.WeightedEntry;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.item.ItemStack;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WeightedEntryMenu extends ChestMenu {
    private final WeightedPatternMenu parent;

    private WeightedPattern parentPattern;
    private WeightedEntry<CreativePattern> entry;

    public WeightedEntryMenu(WeightedPatternMenu parent, WeightedEntry<CreativePattern> entry) {
        super("Edit Weighted Entry", ChestSize.FOUR);
        this.parent = parent;
        this.parentPattern = parent.getPattern();
        this.entry = entry;

        setBreadcrumbs(parent, "Edit Weighted Entry", "Editing entry in pattern...");
    }

    public WeightedPatternMenu getParent() {
        return parent;
    }

    public WeightedPattern getParentPattern() {
        return parent.getPattern();
    }

    public WeightedEntry<CreativePattern> getEntry() {
        return entry;
    }

    public void updateEntryAndDisplay(CreativePlayer player, WeightedEntry<CreativePattern> entry) {
        parentPattern.remove(this.entry.type());
        this.entry = entry;
        parentPattern.put(entry);
        parent.update(player, parentPattern);
        reload().display(player);
    }

    public CreativePattern getPattern() {
        return entry.type();
    }

    public long getTotalWeight() {
        return parentPattern.getTotalWeight();
    }

    @Override
    public void handleClickPlayerInventoryWhileOpen(InventoryPreClickEvent event) {
        event.setCancelled(true);

        CreativePlayer player = (CreativePlayer) event.getPlayer();
        CreativePattern pattern = PatternSelectionMenu.grabPatternFromInventoryClick(event, parentPattern);

        if (pattern != null) {
            entry.withType(pattern);
            updateEntryAndDisplay(player, entry.withType(pattern));
        }
    }

    @Override
    protected void registerButtons() {
        reg().breadcrumbs();

        reg(11, new WeightedEntrySelectPatternButton(this));

        reg(15, new EditWeightButton(this));

        reg(size.bottomCenter(), new GoBackButton(parent).withReloadOnClick(true));

        reg(size.bottomCenter() + 1, new DeleteWeightedEntryButton(this, entry));
    }

    @Override
    public ItemStack renderButton(Player player) {
        Lore lore = new Lore();

        CreativePattern pattern = entry.type();
        int weight = entry.weight();

        Component name = MM."<title>\{pattern.labelMM()}";
        lore.add(MM."<dark_gray>\{pattern.getTypeName()}");
        lore.addEmpty();

        if (!(pattern instanceof SingleBlockPattern)) {
            lore.add(pattern.lore());
            lore.addEmpty();
        }

        if (weight > 0) {
            lore.add(EntryInWeightedTable.weightLore(weight, getTotalWeight()));
            lore.addEmpty();
        }

        lore.add("<cta>Click to edit this entry!");

        return pattern.icon().name(name).lore(lore).build();
    }
}
