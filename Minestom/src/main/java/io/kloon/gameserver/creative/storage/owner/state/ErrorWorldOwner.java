package io.kloon.gameserver.creative.storage.owner.state;

import io.kloon.gameserver.creative.storage.owner.WorldOwner;

public final class ErrorWorldOwner implements WorldOwner.Loaded {
    @Override
    public String getPlayerListLabelMM() {
        return "<dark_red>Error loading name!</dark_red>";
    }
}
