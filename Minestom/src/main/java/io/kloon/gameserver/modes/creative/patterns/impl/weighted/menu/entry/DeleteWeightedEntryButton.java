package io.kloon.gameserver.modes.creative.patterns.impl.weighted.menu.entry;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.patterns.impl.weighted.menu.WeightedPatternMenu;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.weighted.WeightedPattern;
import io.kloon.gameserver.util.weighted.WeightedEntry;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class DeleteWeightedEntryButton implements ChestButton {
    private final WeightedEntryMenu menu;
    private final WeightedEntry<CreativePattern> entry;

    public DeleteWeightedEntryButton(WeightedEntryMenu menu, WeightedEntry<CreativePattern> entry) {
        this.menu = menu;
        this.entry = entry;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        if (menu.getParentPattern().size() <= 1) {
            return;
        }

        CreativePlayer player = (CreativePlayer) p;

        WeightedPatternMenu patternMenu = menu.getParent();
        WeightedPattern pattern = patternMenu.getPattern();
        pattern.remove(entry.type());

        patternMenu.updateAndDisplay(player, pattern);
    }

    @Override
    public ItemStack renderButton(Player player) {
        if (menu.getParentPattern().size() <= 1) {
            return ItemStack.AIR;
        }

        Component name = MM."<title>Delete Entry";

        Lore lore = new Lore();
        lore.wrap("<gray>Remove this entry from the weighted pattern.");
        lore.addEmpty();
        lore.add("<cta>Click to delete!");

        return MenuStack.of(Material.TNT, name, lore);
    }
}
