package io.kloon.gameserver.util;

import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class RandUtil {
    private RandUtil() {}

    public static <T> T getRandom(List<? extends T> list) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        return list.get(rand.nextInt(list.size()));
    }

    public static <T> T getRandom(List<? extends T> list, long seed) {
        Random rand = new Random(seed);
        return list.get(rand.nextInt(list.size()));
    }

    public static <T> T getRandom(T[] array) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        return array[rand.nextInt(array.length)];
    }

    public static <T> T getUnused(Set<T> available, Set<T> used) {
        Set<T> unused = Sets.difference(available, used);
        if (unused.isEmpty()) {
            unused = available;
        }
        return getRandom(new ArrayList<>(unused));
    }
}
