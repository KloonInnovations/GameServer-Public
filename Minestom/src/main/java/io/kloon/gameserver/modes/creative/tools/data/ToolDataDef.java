package io.kloon.gameserver.modes.creative.tools.data;

import java.util.function.Supplier;

public record ToolDataDef<TItemBound, TPlayerBound>(
        Supplier<TItemBound> defaultItemBound,
        Class<TItemBound> itemBoundClass,
        Supplier<TPlayerBound> defaultPlayerBound,
        Class<TPlayerBound> playerBoundClass
) {
    public TItemBound createDefaultItemBound() {
        return defaultItemBound.get();
    }

    public TPlayerBound createDefaultPlayerBound() {
        return defaultPlayerBound.get();
    }
}
