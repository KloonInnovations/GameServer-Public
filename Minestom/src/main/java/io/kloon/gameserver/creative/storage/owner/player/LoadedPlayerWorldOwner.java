package io.kloon.gameserver.creative.storage.owner.player;

import io.kloon.gameserver.creative.storage.owner.WorldOwner.Loaded;
import io.kloon.infra.mongo.accounts.projections.KloonMoniker;

public final class LoadedPlayerWorldOwner implements Loaded {
    private final PlayerWorldOwner owner;
    private final KloonMoniker moniker;

    public LoadedPlayerWorldOwner(PlayerWorldOwner owner, KloonMoniker moniker) {
        this.owner = owner;
        this.moniker = moniker;
    }

    public KloonMoniker getMoniker() {
        return moniker;
    }

    @Override
    public String getPlayerListLabelMM() {
        return STR."<white>\{moniker.minecraftUsername()}</white>";
    }
}
