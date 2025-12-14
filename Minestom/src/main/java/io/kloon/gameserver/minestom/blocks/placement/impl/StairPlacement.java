package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.StairBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.blocks.properties.enums.StairHalf;
import io.kloon.gameserver.minestom.blocks.properties.enums.StairShape;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StairPlacement extends BlockPlacementRule {
    public StairPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @NotNull Block blockUpdate(@NotNull BlockPlacementRule.UpdateState state) {
        StairShape shape = StairBlock.getShape(state.currentBlock(), state.instance(), state.blockPosition());
        return StairBlock.SHAPE.get(shape).on(state.currentBlock());
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        BlockFace face = state.blockFace();
        Block block = PlacementUtils.waterlogged(state);

        StairHalf half = StairHalf.BOTTOM;

        if (face == BlockFace.BOTTOM || face == BlockFace.TOP) {
            half = face == BlockFace.BOTTOM ? StairHalf.TOP : StairHalf.BOTTOM;
        } else {
            Point cursorPos = state.cursorPosition();
            if (cursorPos != null) {
                double y = cursorPos.y() - cursorPos.blockY();
                half = y >= 0.5 ? StairHalf.TOP : StairHalf.BOTTOM;
            }
        }

        block = StairBlock.HALF.get(half).on(block);

        FacingXZ facing = FacingXZ.fromLook(state.playerPosition());
        block = StairBlock.FACING.get(facing).on(block);

        StairShape shape = StairBlock.getShape(block, state.instance(), state.placePosition());
        block = StairBlock.SHAPE.get(shape).on(block);

        return block;
    }
}
