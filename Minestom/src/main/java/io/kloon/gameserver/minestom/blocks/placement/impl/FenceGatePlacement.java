package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.minestom.blocks.handlers.FenceGateBlock;
import io.kloon.gameserver.minestom.blocks.handlers.WallBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.block.BlockUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FenceGatePlacement extends BlockPlacementRule {
    public FenceGatePlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block.Getter instance = state.instance();
        Point blockPosition = state.placePosition();
        FacingXZ facing = FacingXZ.fromLook(state.playerPosition());
        BlockUtils selfBlock = new BlockUtils(instance, blockPosition);

        boolean inWall = switch (facing) {
            case WEST, EAST -> isWall(selfBlock.south()) || isWall(selfBlock.north());
            case SOUTH, NORTH -> isWall(selfBlock.east()) || isWall(selfBlock.west());
        };

        Block block = state.block();
        block = FenceGateBlock.FACING.get(facing).on(block);
        block = FenceGateBlock.IN_WALL.get(inWall).on(block);

        return block;
    }

    private boolean isWall(BlockUtils blockUtils) {
        return WallBlock.BLOCKS.contains(blockUtils.getBlock().defaultState());
    }
}
