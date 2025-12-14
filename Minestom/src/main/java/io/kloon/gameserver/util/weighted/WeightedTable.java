package io.kloon.gameserver.util.weighted;

import org.jetbrains.annotations.UnknownNullability;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class WeightedTable<T> {
    private final Map<T, Integer> weights = new LinkedHashMap<>();
    private long cachedSum;

    public void put(T type, int weight) {
        Integer previousWeight = weights.put(type, weight);
        if (previousWeight != null) {
            cachedSum -= previousWeight;
        }
        this.cachedSum += weight;
    }

    public void remove(T type) {
        Integer weight = weights.remove(type);
        if (weight != null) {
            this.cachedSum -= weight;
        }
    }

    public T roll() {
        return roll(ThreadLocalRandom.current());
    }

    @UnknownNullability
    public T roll(Random rand) {
        if (cachedSum <= 0) {
            return null;
        }

        long roll = rand.nextLong(cachedSum);
        for (Map.Entry<T, Integer> entry : weights.entrySet()) {
            T type = entry.getKey();
            int weight = entry.getValue();
            if (roll < weight) {
                return type;
            }
            roll -= weight;
        }
        return null;
    }

    public long getTotalWeight() {
        return cachedSum;
    }

    public int getWeight(T type) {
        return weights.getOrDefault(type, 0);
    }

    public Set<T> getTypes() {
        return Collections.unmodifiableSet(weights.keySet());
    }

    public List<WeightedEntry<T>> getTypeAndWeights() {
        return weights.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> -e.getValue()))
                .map(e -> new WeightedEntry<>(e.getKey(), e.getValue()))
                .toList();
    }

    public WeightedTable<T> copy() {
        WeightedTable<T> copy = new WeightedTable<>();
        copy.weights.putAll(this.weights);
        copy.cachedSum = this.cachedSum;
        return copy;
    }

}
