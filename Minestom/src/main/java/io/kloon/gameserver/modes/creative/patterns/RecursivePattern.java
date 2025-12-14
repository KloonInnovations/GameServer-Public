package io.kloon.gameserver.modes.creative.patterns;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface RecursivePattern {
    void setParent(CreativePattern pattern);

    @Nullable
    CreativePattern parent();

    Set<CreativePattern> children();

    default List<RecursivePattern> recursiveChildren() {
        List<RecursivePattern> patterns = new ArrayList<>();
        patterns.add(this);
        for (CreativePattern child : children()) {
            if (child instanceof RecursivePattern recursive) {
                patterns.add(recursive);
                patterns.addAll(recursive.recursiveChildren());
            }
        }
        return patterns;
    }

    default int computeRecursionDepth() {
        int depth = 0;
        RecursivePattern pattern =  this;
        while (true) {
            CreativePattern parent = pattern.parent();
            if (parent instanceof RecursivePattern recursive) {
                pattern = recursive;
                ++depth;
            } else {
                break;
            }
        }
        return depth;
    }

    default int computeDeepestChildDepth() {
        return recursiveChildren().stream()
                .mapToInt(RecursivePattern::computeRecursionDepth)
                .max().orElse(0);
    }

    int RECURSION_LIMIT = 2;

    default boolean hasReachedRecursionLimit() {
        return computeRecursionDepth() >= RECURSION_LIMIT;
    }
}
