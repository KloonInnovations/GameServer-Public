package io.kloon.gameserver.minestom.blocks.properties;

import io.kloon.gameserver.minestom.blocks.utils.SegmentedAnglePrecision;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;

public class BannerRotProp {
    private static final SegmentedAnglePrecision SEGMENTED_ANGLE_16 = new SegmentedAnglePrecision(4);

    private final String key;

    public BannerRotProp(String key) {
        this.key = key;
    }

    public BlockProp get(int index) {
        index = Math.clamp(index, 0, 15);
        return new BlockProp(key, String.valueOf(index));
    }

    public BlockProp get(Block block) {
        String property = block.getProperty(key);
        int index = Integer.parseInt(property);
        return get(index);
    }

    public int getSegment(Block block) {
        String property = block.getProperty(key);
        return Integer.parseInt(property);
    }

    public static int convertToSegment(float yaw) {
        return SEGMENTED_ANGLE_16.fromDegrees(yaw);
    }

    public static int convertToSegment(Direction direction) {
        return switch (direction) {
            case SOUTH -> 0;
            case WEST -> 4;
            case NORTH -> 8;
            case EAST -> 12;
            default -> throw new IllegalStateException("Invalid direction " + direction);
        };
    }
}
