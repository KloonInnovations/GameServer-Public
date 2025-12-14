package io.kloon.gameserver.modes.creative.jobs;

import io.kloon.gameserver.modes.creative.history.Change;
import net.minestom.server.collision.BoundingBox;

import java.util.function.BooleanSupplier;

public interface BlocksWork {
    // returns true if finished
    boolean work(BooleanSupplier greenFlag);

    int getPlacedSoFar();

    int getTotalToPlace();

    BoundingBox getBoundingBox();

    default boolean hadOutOfBounds() {
        return false;
    }

    Change getChange();
}
