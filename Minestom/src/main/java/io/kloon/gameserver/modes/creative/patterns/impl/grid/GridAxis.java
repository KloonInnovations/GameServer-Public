package io.kloon.gameserver.modes.creative.patterns.impl.grid;

import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.util.coordinates.Axis;
import net.minestom.server.coordinate.Point;

import java.io.IOException;

public record GridAxis(
        Axis axis,
        boolean enabled,
        int spacing,
        int offset
) {
    public GridAxis withEnabled(boolean enabled) {
        return new GridAxis(axis, enabled, spacing, offset);
    }

    public GridAxis withSpacing(int spacing) {
        return new GridAxis(axis, enabled, spacing, offset);
    }

    public GridAxis withOffset(int offset) {
        return new GridAxis(axis, enabled, spacing, offset);
    }

    public boolean isOnTheLine(Point point) {
        if (!enabled) {
            return false;
        }

        int coord = getCoord(point);

        return coord % (spacing + 1) == offset;
    }

    private int getCoord(Point point) {
        return switch (axis) {
            case X -> point.blockX();
            case Y -> point.blockY();
            case Z -> point.blockZ();
        };
    }

    public static final int DEFAULT_SPACING = 1;
    public static final int MAX_SPACING = 32;

    public static final int DEFAULT_OFFSET = 0;
    public static final int MAX_OFFSET = MAX_SPACING - 1;

    public static GridAxis createDefault(Axis axis) {
        return new GridAxis(axis, true, DEFAULT_SPACING, DEFAULT_OFFSET);
    }

    public static Codec CODEC = new Codec();
    public static class Codec implements MinecraftCodec<GridAxis> {
        @Override
        public void encode(GridAxis gridAxis, MinecraftOutputStream out) throws IOException {
            out.writeByte(gridAxis.axis().ordinal());
            out.writeBoolean(gridAxis.enabled());
            out.writeVarInt(gridAxis.spacing());
            out.writeVarInt(gridAxis.offset());
        }

        @Override
        public GridAxis decode(MinecraftInputStream in) throws IOException {
            return new GridAxis(
                    Axis.values()[in.readUnsignedByte()],
                    in.readBoolean(),
                    in.readVarInt(),
                    in.readVarInt()
            );
        }
    }
}
