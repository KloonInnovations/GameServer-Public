package io.kloon.gameserver.modes.creative.tools.impl.copypaste.selection;

import io.kloon.gameserver.modes.creative.storage.datainworld.PasteSelectionStorage;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteSettings;

public sealed interface PasteSelection permits NoPasteSelection, PastingSelection {
    void tickHolding(CopyPasteSettings settings);

    void tickNotHolding();

    void remove();

    PasteSelectionStorage toStorage();
}
