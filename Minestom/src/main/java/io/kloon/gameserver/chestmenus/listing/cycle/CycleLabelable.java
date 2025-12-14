package io.kloon.gameserver.chestmenus.listing.cycle;

import org.jetbrains.annotations.Nullable;

public interface CycleLabelable {
    String label();

    @Nullable
    default String subLabel() {
        return null;
    }
}
