package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.LanternBlock;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import io.kloon.gameserver.minestom.utils.DirectionUtils;
import io.kloon.gameserver.util.coordinates.Axis;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LanternPlacement extends BlockPlacementRule {
    public LanternPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        BlockFace face = state.blockFace();
        if (DirectionUtils.axis(face) != Axis.Y) {
            return null;
        }

        boolean hanging = face == BlockFace.BOTTOM;

        Block block = PlacementUtils.waterlogged(state);
        block = LanternBlock.HANGING.get(hanging).on(block);
        return block;
    }
}
