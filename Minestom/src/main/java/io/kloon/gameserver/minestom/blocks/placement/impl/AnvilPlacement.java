package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.properties.BlockProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXYZ;
import io.kloon.gameserver.minestom.utils.DirectionUtils;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.Direction;
import net.minestom.server.utils.MathUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AnvilPlacement extends BlockPlacementRule {
    public AnvilPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Direction direction = MathUtils.getHorizontalDirection(state.playerPosition().yaw());
        direction = DirectionUtils.clockwise(direction);
        FacingXYZ facing = FacingXYZ.fromDirection(direction);
        return BlockProp.FACING_XYZ.get(facing).on(state);
    }
}
