package io.kloon.gameserver.modes.creative.history.results;

import java.util.List;

public class ChangeResultList implements ChangeResult {
    private final List<ChangeResult> results;

    public ChangeResultList(List<ChangeResult> results) {
        this.results = results;
    }

    public List<ChangeResult> get() {
        return results;
    }
}
