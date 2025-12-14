package io.kloon.gameserver.modes.creative.selection;

import io.kloon.gameserver.modes.creative.storage.datainworld.SelectionCuboidStorage;

public sealed interface CuboidSelection permits NoCuboidSelection, OneCuboidSelection, TwoCuboidSelection {
    void tick();

    void remove();

    SelectionCuboidStorage toStorage();
}
