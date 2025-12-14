package io.kloon.gameserver.modes.creative.tools.impl.copypaste.selection;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.storage.datainworld.PasteSelectionStorage;
import io.kloon.gameserver.modes.creative.tools.impl.copypaste.CopyPasteSettings;

public final class NoPasteSelection implements PasteSelection {
    private final CreativePlayer player;

    public NoPasteSelection(CreativePlayer player) {
        this.player = player;
    }

    @Override
    public void tickHolding(CopyPasteSettings settings) {

    }

    @Override
    public void tickNotHolding() {

    }

    @Override
    public void remove() {

    }

    @Override
    public PasteSelectionStorage toStorage() {
        return null;
    }
}
