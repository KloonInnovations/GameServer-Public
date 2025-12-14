package io.kloon.gameserver.creative.storage;

import io.kloon.gameserver.creative.storage.defs.WorldDefRepo;
import io.kloon.gameserver.creative.storage.saves.WorldSaveRepo;
import io.kloon.infra.KloonNetworkInfra;

public class CreativeWorldsRepo {
    private final WorldDefRepo defs;
    private final WorldSaveRepo saves;

    public CreativeWorldsRepo(KloonNetworkInfra infra) {
        this.defs = new WorldDefRepo(infra.mongo());
        this.saves = new WorldSaveRepo(infra);
    }

    public WorldDefRepo defs() {
        return defs;
    }

    public WorldSaveRepo saves() {
        return saves;
    }
}
