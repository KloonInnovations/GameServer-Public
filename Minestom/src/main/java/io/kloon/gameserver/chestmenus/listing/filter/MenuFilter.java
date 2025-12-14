package io.kloon.gameserver.chestmenus.listing.filter;

import io.kloon.gameserver.chestmenus.listing.cycle.CycleLabelable;

import java.util.function.Predicate;

public record MenuFilter<T>(
        String label,
        Predicate<T> predicate
) implements CycleLabelable {
}
