package io.kloon.gameserver.modes.creative.history;

import io.kloon.gameserver.modes.creative.history.results.ChangeResult;

public interface Change {
    ChangeType getType();

    ChangeResult undo(ChangeContext ctx);

    ChangeResult redo(ChangeContext ctx);
}
