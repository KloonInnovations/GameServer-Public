package io.kloon.gameserver.modes.creative.patterns.menu;

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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.function.Function;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class SelectPatternWithinPatternButton<T extends CreativePattern> implements ChestButton {
    private final EditPatternMenu<T> menu;
    private final PatternWithinPattern<T> patternWithinPattern;

    public SelectPatternWithinPatternButton(EditPatternMenu<T> menu, PatternWithinPattern<T> patternWithinPattern) {
        this.menu = menu;
        this.patternWithinPattern = patternWithinPattern;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        if (click.isRightClick()) {
            handleRightClick(player, click);
        } else {
            new PatternSelectionMenu(menu, this::onChoosePattern)
                    .parent(menu.getPattern())
                    .display(player);
        }
    }

    public void onChoosePattern(CreativePlayer player, CreativePattern chosen) {
        T pattern = menu.getPattern();
        T updated = patternWithinPattern.edit().apply(pattern, chosen);
        menu.updateAndDisplay(player, updated);
    }

    private void handleRightClick(CreativePlayer player, ButtonClick click) {
        T parent = menu.getPattern();
        CreativePattern pattern = patternWithinPattern.get().apply(parent);
        if (pattern == null || pattern instanceof SingleBlockPattern || !pattern.hasEditMenu()) {
            if (parent instanceof RecursivePattern recursive && recursive.hasReachedRecursionLimit()) {
                player.sendPit(NamedTextColor.RED, "TOO DEEP!", MM."<gray>Reached pattern recursion limit!");
            } else {
                new ChoosePatternMenu(menu, this::onChoosePattern).display(player);
            }
        } else {
            ChestMenu editMenu = pattern.getType().createEditMenu(menu, pattern, this::onChoosePattern);
            editMenu.clickButton(player, click);
        }
    }

    @Override
    public ItemStack renderButton(Player p) {
        T parent = menu.getPattern();
        CreativePattern pattern = patternWithinPattern.get().apply(parent);

        Component name = MM."<title>\{patternWithinPattern.name()}";

        Lore lore = new Lore();
        lore.add(patternWithinPattern.lore());
        if (pattern != null) {
            lore.addEmpty();
            lore.add(MM."<gray>Selected \{pattern.getType().getPropertyName()}");
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
                ? MenuStack.of(patternWithinPattern.iconMat())
                : pattern.icon();

        return builder.name(name).lore(lore).build();
    }
}
