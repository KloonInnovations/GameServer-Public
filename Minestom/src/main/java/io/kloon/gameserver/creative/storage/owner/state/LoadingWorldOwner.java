package io.kloon.gameserver.creative.storage.owner.state;

import io.kloon.gameserver.creative.storage.owner.WorldOwner;

public class LoadingWorldOwner implements WorldOwner.Loaded {
    @Override
    public String getPlayerListLabelMM() {
        return "<dark_gray>Loading...</dark_gray>";
    }
}
