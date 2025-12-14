package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.family.BlockFamily;
import io.kloon.gameserver.minestom.blocks.properties.BlockProp;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.utils.DirectionUtils;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;

import java.util.Set;

public class FenceGateBlock {
    public static final EnumProp<FacingXZ> FACING = BlockProp.FACING_XZ;
    public static final BooleanProp IN_WALL = new BooleanProp("in_wall");
    public static final BooleanProp OPEN = new BooleanProp("open");
    public static final BooleanProp POWERED = new BooleanProp("powered");

    public static boolean isParallel(Block block, Direction direction) {
        FacingXZ facing = FACING.get(block);
        return facing.axis() == DirectionUtils.axis(DirectionUtils.clockwise(direction));
    }

    public static final Set<Block> BLOCKS = BlockFamily.getBlocksOfVariant(BlockFamily.Variant.FENCE_GATE);
}
