package io.kloon.gameserver.modes.creative.patterns.impl.weighted.menu.entry;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.patterns.PatternSelectionMenu;
import io.kloon.gameserver.modes.creative.menu.patterns.use.ChoosePatternMenu;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.RecursivePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.util.weighted.WeightedEntry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WeightedEntrySelectPatternButton implements ChestButton {
    private final WeightedEntryMenu menu;
    private final WeightedEntry<CreativePattern> entry;

    public WeightedEntrySelectPatternButton(WeightedEntryMenu menu) {
        this.menu = menu;
        this.entry = menu.getEntry();
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        if (click.isRightClick()) {
            handleRightClick(player, click);
        } else {
            new PatternSelectionMenu(menu, this::onChoosePattern)
                    .parent(menu.getParentPattern())
                    .display(p);
        }
    }

    private void handleRightClick(CreativePlayer player, ButtonClick click) {
        CreativePattern pattern = entry.type();
        if (pattern == null || pattern instanceof SingleBlockPattern || !pattern.hasEditMenu()) {
            if (menu.getParentPattern() instanceof RecursivePattern recursive && recursive.hasReachedRecursionLimit()) {
                player.sendPit(NamedTextColor.RED, "TOO DEEP!", MM."<gray>Reached pattern recursion limit!");
            } else {
                new ChoosePatternMenu(menu, this::onChoosePattern).display(player);
            }
        } else {
            ChestMenu editMenu = pattern.getType().createEditMenu(menu, pattern, this::onChoosePattern);
            editMenu.clickButton(player, click);
        }
    }

    private void onChoosePattern(CreativePlayer player, CreativePattern pattern) {
        WeightedEntry<CreativePattern> updatedEntry = entry.withType(pattern);
        menu.updateEntryAndDisplay(player, updatedEntry);
    }

    @Override
    public ItemStack renderButton(Player player) {
        CreativePattern pattern = entry.type();

        Component name = MM."<title>Select Block";

        Lore lore = new Lore();
        lore.wrap("<gray>Select a block (or pattern) for this weighted pattern entry.");
        if (pattern != null) {
            lore.addEmpty();
            lore.add(MM."<gray>Entry \{pattern.getType().getPropertyName()}");
            lore.add(pattern.lore());
        }

        lore.addEmpty();
        if (pattern == null || pattern instanceof SingleBlockPattern || !pattern.hasEditMenu()) {
            lore.add("<rcta>Click to pick a pattern!");
            lore.add("<lcta>Click to select a block!");
        } else {
            lore.add("<rcta>Click to edit pattern!");
            lore.add("<lcta>Click to select a block!");
        }

        ItemBuilder2 builder = pattern == null
                ? MenuStack.of(Material.PINK_GLAZED_TERRACOTTA)
                : pattern.icon();

        return builder.name(name).lore(lore).build();
    }
}
