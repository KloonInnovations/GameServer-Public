package io.kloon.gameserver.chestmenus.listing.search;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.ToStringFunction;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MenuSearch<T> {
    private final Function<T, String> stringFunction;

    private String query;

    private boolean fuzzy = false;

    public MenuSearch(Function<T, String> stringFunction) {
        this.stringFunction = stringFunction;
    }

    public MenuSearch<T> fuzzy(boolean fuzzy) {
        this.fuzzy = fuzzy;
        return this;
    }

    public boolean isFuzzy() {
        return fuzzy;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Nullable
    public String getQuery() {
        return query;
    }

    public List<T> apply(List<T> things) {
        if (query == null) {
            return things;
        }

        if (fuzzy) {
            ToStringFunction<T> toString = stringFunction::apply;
            return FuzzySearch.extractAll(query, things, toString, 65).stream()
                    .map(BoundExtractedResult::getReferent)
                    .collect(Collectors.toList());
        } else {
            String queryLower = query.toLowerCase();
            return things.stream()
                    .filter(thing -> {
                        String thingName = stringFunction.apply(thing).toLowerCase();
                        return thingName.contains(queryLower);
                    }).collect(Collectors.toList());
        }
    }
}
