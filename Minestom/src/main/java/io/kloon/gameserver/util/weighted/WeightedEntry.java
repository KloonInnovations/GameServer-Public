package io.kloon.gameserver.util.weighted;

public record WeightedEntry<T>(T type, int weight) {
    public WeightedEntry<T> withType(T type) {
        return new WeightedEntry<>(type, weight);
    }

    public WeightedEntry<T> withWeight(int weight) {
        return new WeightedEntry<>(type, weight);
    }
}
