package io.kloon.gameserver.creative.menu.create;

import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.saves.WorldSave;
import io.kloon.gameserver.creative.storage.saves.WorldSaveWithData;

public record CopyingWorld(
        WorldDef worldDef,
        WorldSaveWithData saveWithData
) {
    public WorldSave save() {
        return saveWithData.worldSave();
    }
}
