package io.kloon.gameserver.minestom.blocks.utils;

import io.kloon.gameserver.minestom.blocks.handlers.WaterBlock;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.Direction;
import net.minestom.server.utils.MathUtils;

import java.util.Arrays;
import java.util.Comparator;

public class PlacementUtils {
    public static Block waterlogged(BlockPlacementRule.PlacementState state) {
        Point placePos = state.placePosition();
        Block existingBlock = state.instance().getBlock(placePos);

        boolean inWater = existingBlock == Block.WATER || WaterBlock.WATERLOGGED.is(existingBlock);
        return WaterBlock.WATERLOGGED.get(inWater).on(state);
    }

    public static Direction getHorizontalDirection(BlockPlacementRule.PlacementState state) {
        return getHorizontalDirection(state.playerPosition().yaw());
    }

    public static Direction getHorizontalDirection(float yaw) {
        return MathUtils.getHorizontalDirection(yaw);
    }

    public static Direction getLookingDirection(BlockPlacementRule.PlacementState state) {
        return getLookingDirection(state.playerPosition().direction());
    }

    public static Direction getLookingDirection(Vec normalizedVec) {
        return Arrays.stream(Direction.values())
                .max(Comparator.comparingDouble(dir -> {
                    Vec vec = new Vec(dir.normalX(), dir.normalY(), dir.normalZ());
                    return vec.dot(normalizedVec);
                })).orElseThrow(() -> new IllegalStateException("Invalid looking vec"));
    }
}
