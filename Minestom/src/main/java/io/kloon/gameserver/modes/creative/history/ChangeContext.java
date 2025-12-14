package io.kloon.gameserver.modes.creative.history;

import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;

public record ChangeContext(
        ChangeRecord record,
        CreativePlayer player,
        CreativeInstance instance
) {
}
