package io.kloon.gameserver.minestom.blocks.transforms;

import io.kloon.gameserver.minestom.utils.DirectionUtils;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.utils.Direction;

public enum RotationTransform {
    NONE,
    CLOCKWISE_90,
    CLOCKWISE_180,
    CLOCKWISE_270,
    ;


    public Direction rotate(Direction direction) {
        return switch (this) {
            case NONE -> direction;
            case CLOCKWISE_90 -> DirectionUtils.clockwise(direction);
            case CLOCKWISE_180 -> direction.opposite();
            case CLOCKWISE_270 -> DirectionUtils.counterwise(direction);
        };
    }

    public BlockFace rotate(BlockFace blockFace) {
        return switch (this) {
            case NONE -> blockFace;
            case CLOCKWISE_90 -> DirectionUtils.clockwise(blockFace);
            case CLOCKWISE_180 -> blockFace.getOppositeFace();
            case CLOCKWISE_270 -> DirectionUtils.counterwise(blockFace);
        };
    }
}
