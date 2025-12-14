package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import io.kloon.gameserver.minestom.utils.DirectionUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.kloon.gameserver.minestom.blocks.handlers.TrapdoorBlock.*;

public class TrapdoorPlacement extends BlockPlacementRule {
    public TrapdoorPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = PlacementUtils.waterlogged(state);

        FacingXZ facing = FacingXZ.fromLook(state.playerPosition());
        BlockFace clickedFace = state.blockFace();
        Point cursorPos = state.cursorPosition();

        if (DirectionUtils.axis(clickedFace).vertical()) {
            block = FACING.get(facing.opposite()).on(block);
            block = HALF.get(clickedFace == BlockFace.TOP ? Half.BOTTOM : Half.TOP).on(block);
        } else {
            block = FACING.get(FacingXZ.fromBlockFace(clickedFace)).on(block);
            block = HALF.get(cursorPos.y() > 0.5 ? Half.TOP : Half.BOTTOM).on(block);
        }

        return block;
    }
}
