package io.kloon.gameserver.util.weighted;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.util.formatting.NumberFmt;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public record EntryInWeightedTable<T>(
        WeightedTable<T> table,
        WeightedEntry<T> entry
) {
    public Lore weightLore() {
        int weight = entry.weight();
        long total = table.getTotalWeight();
        return weightLore(weight, total);
    }

    public static Lore weightLore(int weight, long total) {
        Lore lore = new Lore();
        lore.add(MM."<gray>Weight: <yellow>\{NumberFmt.NO_DECIMAL.format(weight)}<gray>/\{NumberFmt.NO_DECIMAL.format(total)}");

        if (total > 0) {
            double percent = ((double) weight / total) * 100;
            lore.add(MM."<gray>Percent: <aqua>\{NumberFmt.TWO_DECIMAL.format(percent)}%");
        }

        return lore;
    }
}
