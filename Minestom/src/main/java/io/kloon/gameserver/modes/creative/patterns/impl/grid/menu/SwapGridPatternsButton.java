package io.kloon.gameserver.modes.creative.patterns.impl.grid.menu;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.grid.GridPattern;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class SwapGridPatternsButton implements ChestButton {
    private final GridPatternMenu menu;

    public SwapGridPatternsButton(GridPatternMenu menu) {
        this.menu = menu;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        GridPattern pattern = menu.getPattern();

        CreativePattern lines = pattern.getLines();
        CreativePattern inBetween = pattern.getInBetween();

        pattern = pattern.withLines(inBetween);
        pattern = pattern.withInBetween(lines);

        ToolDataType.PATTERN_BOUND.sendPit(player, MM."<gray>Swapped the grid's patterns!");

        menu.updateAndDisplay(player, pattern);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Swap!";

        Lore lore = new Lore();
        lore.wrap("<gray>Swap the patterns of the grid between eachothers.");
        lore.addEmpty();
        lore.add("<cta>Click to swap!");

        return MenuStack.of(Material.SWEET_BERRIES, name, lore);
    }
}
