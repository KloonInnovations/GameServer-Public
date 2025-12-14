package io.kloon.gameserver.creative.menu.manage.oldsaves;

import io.kloon.gameserver.creative.storage.defs.WorldDef;
import io.kloon.gameserver.creative.storage.saves.WorldSave;
import org.jetbrains.annotations.Nullable;

public record OldWorldSave(
        WorldDef world,
        WorldSave save,
        @Nullable WorldSave prev, // chronologically
        boolean isLatest
) {
}
