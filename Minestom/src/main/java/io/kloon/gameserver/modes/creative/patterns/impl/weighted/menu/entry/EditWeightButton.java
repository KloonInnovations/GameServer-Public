package io.kloon.gameserver.modes.creative.patterns.impl.weighted.menu.entry;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.signui.SignUX;
import io.kloon.gameserver.chestmenus.signui.input.SignUXNumberInput;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.util.weighted.EntryInWeightedTable;
import io.kloon.gameserver.util.weighted.WeightedEntry;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class EditWeightButton implements ChestButton {
    private final WeightedEntryMenu menu;
    private final WeightedEntry<CreativePattern> entry;

    public static final int RIGHT_CLICK_ADD = 10;

    public EditWeightButton(WeightedEntryMenu menu) {
        this.menu = menu;
        this.entry = menu.getEntry();
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        if (click.isRightClick()) {
            int weight = entry.weight();
            weight = weight <= 0
                    ? RIGHT_CLICK_ADD
                    : weight + RIGHT_CLICK_ADD;
            onInput(player, weight);
            player.playSound(SoundEvent.BLOCK_NOTE_BLOCK_BASEDRUM, 0.6f + 0.03f * weight);
        } else {
            String[] inputLines = SignUX.inputLines("Enter weight", "(min 1)");
            SignUX.display(player, inputLines, new SignUXNumberInput().min(1)
                    .build(player, input -> onInput(player, input.intValue())));
        }
    }

    public void onInput(CreativePlayer player, int weight) {
        WeightedEntry<CreativePattern> updated = menu.getEntry().withWeight(weight);
        menu.updateEntryAndDisplay(player, updated);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Edit Weight";

        Lore lore = new Lore();
        lore.wrap("<gray>The relative chance for this block/pattern to be selected.");
        lore.addEmpty();

        int weight = entry.weight();
        long total = menu.getTotalWeight();
        if (weight > 0) {
            lore.add(EntryInWeightedTable.weightLore(weight, total));
            lore.addEmpty();

            lore.add(MM."<rcta>Click to add \{RIGHT_CLICK_ADD}!");
            lore.add("<lcta>Click to edit weight!");
        } else {
            lore.add("<dark_gray>No weight set yet!");
            lore.addEmpty();
            lore.add(MM."<rcta>Click to add \{RIGHT_CLICK_ADD}!");
            lore.add("<lcta>Click to set weight!");
        }

        return MenuStack.of(Material.ANVIL, name, lore);
    }
}
