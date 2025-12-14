package io.kloon.gameserver.modes.creative.menu.util;

import io.kloon.gameserver.modes.creative.CreativePlayer;

import java.util.function.BiConsumer;

public interface CreativeConsumer<T> extends BiConsumer<CreativePlayer, T> {
    static <T> CreativeConsumer<T> empty() {
        return (_, _) -> {};
    }
}
