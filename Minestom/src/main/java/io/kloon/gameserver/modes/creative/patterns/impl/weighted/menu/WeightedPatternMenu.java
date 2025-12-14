package io.kloon.gameserver.modes.creative.patterns.impl.weighted.menu;

import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.builtin.GoBackButton;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.patterns.CopyPatternToInventoryButton;
import io.kloon.gameserver.modes.creative.patterns.impl.weighted.menu.entry.AddWeightedEntryButton;
import io.kloon.gameserver.modes.creative.patterns.impl.weighted.menu.entry.WeightedEntryMenu;
import io.kloon.gameserver.modes.creative.menu.util.CreativeConsumer;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.weighted.WeightedPattern;
import io.kloon.gameserver.util.weighted.WeightedEntry;

import java.util.List;
import java.util.stream.Collectors;

public class WeightedPatternMenu extends ChestMenu {
    private final ChestMenu parent;

    private WeightedPattern pattern;
    private final CreativeConsumer<CreativePattern> update;

    public WeightedPatternMenu(ChestMenu parent, WeightedPattern pattern, CreativeConsumer<CreativePattern> update) {
        super("Edit Weighted Pattern");
        this.parent = parent;
        this.pattern = pattern;
        this.update = update;

        setBreadcrumbs(parent, "Edit Weighted Pattern", "Editing weighted pattern...");
    }

    public WeightedPattern getPattern() {
        return pattern;
    }

    public void update(CreativePlayer player, WeightedPattern updated) {
        this.pattern = updated;
        update.accept(player, updated);
    }

    public void updateAndDisplay(CreativePlayer player, WeightedPattern updated) {
        update(player, updated);
        reload().display(player);
    }

    @Override
    protected void registerButtons() {
        List<WeightedEntry<CreativePattern>> entries = pattern.getChildrenAndWeights();

        List<ChestButton> buttons = entries.stream()
                .map(entry -> new WeightedEntryMenu(this, entry))
                .collect(Collectors.toList());

        if (entries.size() < WeightedPattern.MAX_ENTRIES) {
            buttons.add(new AddWeightedEntryButton(this));
        }

        ChestLayouts.INSIDE.distribute(buttons, this::reg);

        reg().breadcrumbs();
        reg(size.bottomCenter(), new GoBackButton(parent));
        reg(size.bottomCenter() + 1, new CopyPatternToInventoryButton(pattern));
    }
}
