package io.kloon.gameserver.chestmenus.listing.cycle;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Cycle<T> {
    private final List<T> list = new ArrayList<>();
    private int index;

    public Cycle() {
    }

    public Cycle(Collection<? extends T> collection) {
        list.addAll(collection);
    }

    public Cycle(T[] array) {
        this(Arrays.asList(array));
    }

    public static <T extends Enum<T>> Cycle<T> fromEnum(T[] values, T selected) {
        Cycle<T> cycle = new Cycle<>(values);
        cycle.select(selected);
        return cycle;
    }

    public List<T> asList() {
        return Collections.unmodifiableList(list);
    }

    public Cycle<T> select(T obj) {
        int selectIndex = list.indexOf(obj);
        if (selectIndex < 0) return this;
        this.index = selectIndex;
        return this;
    }

    public Cycle<T> add(T obj) {
        list.add(obj);
        return this;
    }

    public int size() {
        return list.size();
    }

    @Nullable
    public T getSelected() {
        if (list.isEmpty()) return null;
        return list.get(index % list.size());
    }

    @Nullable
    public T getFirst() {
        if (list.isEmpty()) return null;
        return list.getFirst();
    }

    @Nullable
    public T goBackwards() {
        if (list.isEmpty()) return null;

        --index;
        if (index < 0) {
            index = list.size() - 1;
        }
        return getSelected();
    }

    @Nullable
    public T goForward() {
        if (list.isEmpty()) return null;

        ++index;
        if (index >= list.size()) {
            index = 0;
        }
        return getSelected();
    }
}
