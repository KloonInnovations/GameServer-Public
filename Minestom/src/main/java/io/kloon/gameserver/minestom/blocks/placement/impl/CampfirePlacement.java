package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.CampfireBlock;
import io.kloon.gameserver.minestom.blocks.handlers.FacingXZBlock;
import io.kloon.gameserver.minestom.blocks.handlers.WaterBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CampfirePlacement extends BlockPlacementRule {
    public CampfirePlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = PlacementUtils.waterlogged(state);
        boolean inWater = WaterBlock.WATERLOGGED.is(block);

        boolean lit = !inWater;

        Block blockBelow = state.instance().getBlock(state.placePosition().relative(BlockFace.BOTTOM));
        boolean signal = CampfireBlock.isSmokeSource(blockBelow);

        FacingXZ facing = FacingXZ.fromLook(state.playerPosition()).opposite();

        block = CampfireBlock.LIT.get(lit).on(block);
        block = CampfireBlock.SIGNAL_FIRE.get(signal).on(block);
        block = FacingXZBlock.FACING_XZ.get(facing).on(block);

        return block;
    }
}
