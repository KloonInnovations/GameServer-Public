package io.kloon.gameserver.minestom.blocks.properties.enums;

import net.minestom.server.coordinate.Vec;

import java.util.Arrays;
import java.util.List;

import static io.kloon.gameserver.util.coordinates.CardinalDirection.*;

public enum RailShape {
    NORTH_SOUTH(false, NORTH.vec(), SOUTH.vec()),
    EAST_WEST(false, WEST.vec(), EAST.vec()),
    ASCENDING_EAST(true, WEST.vec(), EAST.vec().add(0, 1, 0)),
    ASCENDING_WEST(true, WEST.vec().add(0, 1, 0), EAST.vec()),
    ASCENDING_NORTH(true, NORTH.vec().add(0, 1, 0), SOUTH.vec()),
    ASCENDING_SOUTH(true, NORTH.vec(), SOUTH.vec().add(0, 1, 0)),
    SOUTH_EAST(false, EAST.vec(), SOUTH.vec()),
    SOUTH_WEST(false, WEST.vec(), SOUTH.vec()),
    NORTH_WEST(false, WEST.vec(), NORTH.vec()),
    NORTH_EAST(false, EAST.vec(), NORTH.vec()),
    ;

    private final boolean ascending;
    private final List<Vec> connectionVecs;

    RailShape(boolean ascending, Vec... connectionVecs) {
        this.ascending = ascending;
        this.connectionVecs = Arrays.asList(connectionVecs);
    }

    public List<Vec> connectionVecs() {
        return connectionVecs;
    }
}
