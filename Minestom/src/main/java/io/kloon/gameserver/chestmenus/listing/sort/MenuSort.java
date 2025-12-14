package io.kloon.gameserver.chestmenus.listing.sort;

import io.kloon.gameserver.chestmenus.listing.cycle.CycleLabelable;

import java.util.Comparator;

public record MenuSort<T>(
        String label,
        Comparator<T> sort
) implements CycleLabelable {
}
