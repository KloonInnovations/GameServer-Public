package io.kloon.gameserver.util;

import java.util.Objects;
import java.util.function.Consumer;

public class ChangeTracker<T> {
    private T value;

    public void acceptIfChanged(T newValue, Consumer<T> consumer) {
        boolean same = Objects.equals(value, newValue);
        this.value = newValue;
        if (!same) {
            consumer.accept(newValue);
        }
    }
}
