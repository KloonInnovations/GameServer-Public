package io.kloon.gameserver.modes.creative.history.results;

public record ExceptionResult(
        Throwable throwable
) implements ChangeResult {
}
