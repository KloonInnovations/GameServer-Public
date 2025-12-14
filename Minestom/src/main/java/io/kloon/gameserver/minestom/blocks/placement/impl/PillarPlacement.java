package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.PillarBlock;
import io.kloon.gameserver.minestom.utils.DirectionUtils;
import io.kloon.gameserver.util.coordinates.Axis;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PillarPlacement extends BlockPlacementRule {
    private boolean waterlogged = false;

    public PillarPlacement(@NotNull Block block) {
        super(block);
    }

    public PillarPlacement waterlogged() {
        this.waterlogged = true;
        return this;
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        BlockFace face = state.blockFace();
        if (face == null) {
            return state.block();
        }
        Axis axis = DirectionUtils.axis(face);

        return PillarBlock.AXIS.get(axis).on(state);
    }
}
