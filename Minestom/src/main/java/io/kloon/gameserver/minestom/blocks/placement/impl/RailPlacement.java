package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.RailBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.blocks.properties.enums.RailShape;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RailPlacement extends BlockPlacementRule {
    public RailPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @NotNull Block blockUpdate(@NotNull BlockPlacementRule.UpdateState state) {
        if (!(state.instance() instanceof Instance instance)) {
            return super.blockUpdate(state);
        }

        Point blockPos = state.blockPosition();
        if (!RailBlock.isRail(instance, blockPos)) {
            return super.blockUpdate(state);
        }

        RailBlock.RailState railState = new RailBlock.RailState(instance, blockPos, state.currentBlock());
        RailShape inputShape = RailBlock.RAIL_SHAPE.get(state.currentBlock());
        return railState.place(inputShape).getBlock();
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = PlacementUtils.waterlogged(state);

        Direction horizontalDir = FacingXZ.fromLook(state.playerPosition()).toDirection();
        boolean xAxis = horizontalDir == Direction.EAST || horizontalDir == Direction.WEST;

        RailShape shape = xAxis ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH;

        if (state.instance() instanceof Instance instance) {
            RailBlock.RailState railState = new RailBlock.RailState(instance, state.placePosition(), block);
            return railState.place(shape).getBlock();
        } else {
            return RailBlock.RAIL_SHAPE.get(shape).on(block);
        }
    }
}
