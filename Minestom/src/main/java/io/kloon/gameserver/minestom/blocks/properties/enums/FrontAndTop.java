package io.kloon.gameserver.minestom.blocks.properties.enums;

import io.kloon.gameserver.util.WordUtilsK;
import io.kloon.infra.util.EnumQuery;
import net.minestom.server.utils.Direction;

public enum FrontAndTop {
    DOWN_EAST(Direction.DOWN, Direction.EAST),
    DOWN_NORTH(Direction.DOWN, Direction.NORTH),
    DOWN_SOUTH(Direction.DOWN, Direction.SOUTH),
    DOWN_WEST(Direction.DOWN, Direction.WEST),
    UP_EAST(Direction.UP, Direction.EAST),
    UP_NORTH(Direction.UP, Direction.NORTH),
    UP_SOUTH(Direction.UP, Direction.SOUTH),
    UP_WEST(Direction.UP, Direction.WEST),
    WEST_UP(Direction.WEST, Direction.UP),
    EAST_UP(Direction.EAST, Direction.UP),
    NORTH_UP(Direction.NORTH, Direction.UP),
    SOUTH_UP(Direction.SOUTH, Direction.UP),
    ;

    private final String key;
    private final Directions directions;

    FrontAndTop(Direction front, Direction top) {
        this.key = WordUtilsK.enumName(this);
        this.directions = new Directions(front, top);
    }

    public String getKey() {
        return key;
    }

    public Direction front() {
        return directions.front();
    }

    public Direction top() {
        return directions.top();
    }

    private record Directions(Direction front, Direction top) {}

    private static final FrontAndTop[] VALUES = values();
    private static final EnumQuery<Directions, FrontAndTop> BY_DIRECTIONS = new EnumQuery<>(FrontAndTop.values(), f -> f.directions);

    public static FrontAndTop from(Direction front, Direction top) {
        return BY_DIRECTIONS.get(new Directions(front, top));
    }
}
