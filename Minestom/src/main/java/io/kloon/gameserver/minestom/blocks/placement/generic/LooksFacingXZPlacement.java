package io.kloon.gameserver.minestom.blocks.placement.generic;

import io.kloon.gameserver.minestom.blocks.handlers.FacingXZBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.Direction;
import net.minestom.server.utils.MathUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// opposite by default, actually (because most blocks are)
public class LooksFacingXZPlacement extends BlockPlacementRule {
    private boolean waterlogged = false;
    private boolean opposite = false;

    public LooksFacingXZPlacement(@NotNull Block block) {
        super(block);
    }

    public LooksFacingXZPlacement waterlogged() {
        this.waterlogged = true;
        return this;
    }

    public LooksFacingXZPlacement opposite() {
        this.opposite = true;
        return this;
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        float yaw = state.playerPosition().yaw();
        Direction dir = MathUtils.getHorizontalDirection(yaw).opposite();
        if (opposite) {
            dir = dir.opposite();
        }
        FacingXZ facing = FacingXZ.fromDirection(dir);

        Block block = state.block();
        if (waterlogged) {
            block = PlacementUtils.waterlogged(state);
        }
        block = FacingXZBlock.FACING_XZ.get(facing).on(block);
        return block;
    }
}
