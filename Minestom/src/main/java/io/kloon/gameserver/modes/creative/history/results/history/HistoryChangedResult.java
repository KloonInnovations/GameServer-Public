package io.kloon.gameserver.modes.creative.history.results.history;

import io.kloon.gameserver.modes.creative.history.ChangeRecord;
import io.kloon.gameserver.modes.creative.history.results.ChangeResult;

public record HistoryChangedResult(ChangeRecord initialRecord, ChangeResult changeResult) implements HistoryEditResult {
}
