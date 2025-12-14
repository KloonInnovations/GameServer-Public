package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.HopperBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingHopper;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HopperPlacement extends BlockPlacementRule {
    public HopperPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        BlockFace oppositeFace = state.blockFace().getOppositeFace();

        FacingHopper facing = FacingHopper.fromBlockFace(oppositeFace);

        Block block = HopperBlock.ENABLED.get(true).on(state.block());
        block = HopperBlock.FACING.get(facing).on(block);

        return block;
    }
}
