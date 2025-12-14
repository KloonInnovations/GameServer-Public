package io.kloon.gameserver.modes.creative.jobs;

import io.kloon.gameserver.modes.creative.history.Change;

public record JobCompletion(
        boolean cancelled,
        long wallNanos,
        long cpuNanos,
        boolean hadOutOfBounds,
        Change change
) {
}
