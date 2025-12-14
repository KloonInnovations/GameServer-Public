package io.kloon.gameserver.modes.creative.tools.impl.blend;

import io.kloon.gameserver.chestmenus.listing.cycle.CycleLabelable;
import io.kloon.gameserver.util.WordUtilsK;
import io.kloon.gameserver.util.coordinates.CardinalDirection;
import io.kloon.infra.util.EnumQuery;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum BlendSampling implements CycleLabelable {
    CARDINAL("cardinal"),
    CUBE("cube"),
    DOUBLE("double"),
    ;

    private final String dbKey;
    private final Vec[] offsets;
    private final String name;

    BlendSampling(String dbKey) {
        this.dbKey = dbKey;
        this.offsets = switch (this) {
            case CARDINAL -> Arrays.stream(CardinalDirection.values())
                    .map(CardinalDirection::vec)
                    .toArray(Vec[]::new);
            case CUBE -> rangeToOffsets(new int[] {-1, 0, 1});
            case DOUBLE -> rangeToOffsets(new int[] { -2, -1, 0, 1, 2 });
        };
        this.name = WordUtilsK.enumName(this);
    }

    public String getDbKey() {
        return dbKey;
    }

    public Vec[] getOffsets() {
        return offsets;
    }

    @Override
    public String label() {
        return name;
    }

    @Override
    public String subLabel() {
        return switch (this) {
            case CARDINAL -> "Connected Faces";
            case CUBE -> "3x3x3 Cube";
            case DOUBLE -> "5x5x5 Cube";
        };
    }

    private static Vec[] rangeToOffsets(int[] range) {
        int volume = range.length * range.length * range.length;
        Vec[] offsets = new Vec[volume];
        int i = 0;
        for (int x : range) {
            for (int y : range) {
                for (int z : range) {
                    offsets[i] = new Vec(x, y, z);
                    ++i;
                }
            }
        }
        return offsets;
    }

    public static final EnumQuery<String, BlendSampling> BY_DB_KEY = new EnumQuery<>(values(), s -> s.dbKey);
}
