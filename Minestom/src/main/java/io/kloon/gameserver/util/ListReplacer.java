package io.kloon.gameserver.util;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ListReplacer {
    public static <TObj, TProperty> UnaryOperator<TObj> by(Function<TObj, TProperty> propertyExtractor, TObj replacement) {
        TProperty desired = propertyExtractor.apply(replacement);
        return other -> Objects.equals(propertyExtractor.apply(other), desired) ? replacement : other;
    }
}
