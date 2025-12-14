package io.kloon.gameserver.modes.creative.tools.impl.replace.menu.entry;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.patterns.PatternSelectionMenu;
import io.kloon.gameserver.modes.creative.menu.patterns.use.ChoosePatternMenu;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.PatternType;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.tools.impl.replace.menu.ReplacementEntryMenu;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WhatToReplaceWithButton implements ChestButton {
    private final ReplacementEntryMenu menu;

    public WhatToReplaceWithButton(ReplacementEntryMenu menu) {
        this.menu = menu;
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        CreativePattern existingPattern = menu.getWhatToReplaceWith();
        if (click.isRightClick()) {
            if (existingPattern == null || existingPattern instanceof SingleBlockPattern || !existingPattern.hasEditMenu()) {
                new ChoosePatternMenu(menu, this::onSelectReplacement).display(player);
            } else {
                ChestButton editButton = existingPattern.getType().createEditMenu(menu, existingPattern, this::onSelectReplacement);
                editButton.clickButton(player, click);
            }
        } else {
            new PatternSelectionMenu(menu, this::onSelectReplacement)
                    .editing(existingPattern)
                    .display(player);
        }
    }

    private void onSelectReplacement(CreativePlayer player, CreativePattern pattern) {
        menu.setWhatToReplaceWith(pattern);
        menu.display(player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>What To Replace With";
        Lore lore = new Lore();

        CreativePattern whatToReplaceWith = menu.getWhatToReplaceWith();
        if (whatToReplaceWith == null) {
            lore.wrap("<gray>What block/pattern to replace with.");
            lore.addEmpty();
            lore.add("<cta>Click to pick replacement!");
            return MenuStack.of(Material.MUSIC_DISC_PRECIPICE, name, lore);
        }

        PatternType patternType = whatToReplaceWith.getType();

        lore.addEmpty();
        lore.add(MM."<gray>Replacement \{patternType.getPropertyName()}");
        lore.add(whatToReplaceWith.lore());

        lore.addEmpty();
        if (whatToReplaceWith instanceof SingleBlockPattern || !whatToReplaceWith.hasEditMenu()) {
            lore.add("<rcta>Click to pick a pattern!");
        } else {
            lore.add("<rcta>Click to edit pattern!");
        }
        lore.add("<lcta>Click to pick from menu!");

        return whatToReplaceWith.icon().name(name).lore(lore).build();
    }
}
