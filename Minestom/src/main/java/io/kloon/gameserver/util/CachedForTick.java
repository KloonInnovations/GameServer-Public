package io.kloon.gameserver.util;

import io.kloon.gameserver.minestom.GlobalMinestomTicker;

import java.util.function.Supplier;

public class CachedForTick<T> {
    private T value;
    private int tick;

    private Supplier<T> supplier;

    public CachedForTick(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        int thisTick = GlobalMinestomTicker.getTick();
        if (thisTick != tick || value == null) {
            this.tick = thisTick;
            this.value = supplier.get();
        }
        return value;
    }

    public void reset() {
        this.value = null;
    }
}
