package io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.stored;

import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.WorldClip;
import org.jetbrains.annotations.Nullable;

public record NoClipboardEntry() implements StoredClipboardEntry {
    @Override
    public @Nullable WorldClip toClip() {
        return null;
    }
}
