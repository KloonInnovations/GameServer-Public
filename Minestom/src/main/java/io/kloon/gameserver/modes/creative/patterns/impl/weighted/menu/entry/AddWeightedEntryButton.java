package io.kloon.gameserver.modes.creative.patterns.impl.weighted.menu.entry;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.blocks.family.ColorFamily;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.patterns.impl.weighted.menu.WeightedPatternMenu;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.patterns.impl.weighted.WeightedPattern;
import io.kloon.gameserver.util.RandUtil;
import io.kloon.gameserver.util.weighted.WeightedEntry;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class AddWeightedEntryButton implements ChestButton {
    private final WeightedPatternMenu menu;

    public AddWeightedEntryButton(WeightedPatternMenu menu) {
        this.menu = menu;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        CreativePattern defaultPattern = computePatternToAdd();
        WeightedEntry<CreativePattern> entry = new WeightedEntry<>(defaultPattern, 10);

        WeightedPattern pattern = menu.getPattern();
        pattern.put(entry);

        menu.update(player, pattern);
        new WeightedEntryMenu(menu, entry).display(player);
    }

    private CreativePattern computePatternToAdd() {
        Set<ColorFamily> available = new HashSet<>(ColorFamily.getAll());
        Set<ColorFamily> used = menu.getPattern().children().stream()
                .map(p -> p instanceof SingleBlockPattern single ? single.getBlock() : null)
                .map(ColorFamily::getFamily)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        ColorFamily color = RandUtil.getUnused(available, used);
        return new SingleBlockPattern(color.woolBlock());
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Create Entry";

        Lore lore = new Lore();
        lore.wrap("<gray>Add a new weighted entry to this pattern.");
        lore.addEmpty();
        lore.add("<cta>Click to create!");

        return MenuStack.of(Material.LIME_CONCRETE, name, lore);
    }
}
