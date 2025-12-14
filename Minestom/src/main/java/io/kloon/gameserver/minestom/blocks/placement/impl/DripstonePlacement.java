package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.DripstoneBlock;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DripstonePlacement extends BlockPlacementRule {
    public DripstonePlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @NotNull Block blockUpdate(@NotNull BlockPlacementRule.UpdateState state) {
        Block block = state.currentBlock();

        DripstoneBlock.Verticality verticality = DripstoneBlock.VERTICALITY.get(block);

        DripstoneBlock.Thickness thickness = DripstoneBlock.computeThickness(state.instance(), state.blockPosition(), verticality.direction(), true);

        return DripstoneBlock.THICKNESS.get(thickness).on(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        BlockFace clickedFace = state.blockFace();

        DripstoneBlock.Verticality verticality = getTipDirection(state.instance(), state.placePosition(), clickedFace);
        if (verticality == null) {
            return null;
        }

        boolean notSneaking = !state.isPlayerShifting();
        DripstoneBlock.Thickness thickness = DripstoneBlock.computeThickness(state.instance(), state.placePosition(), verticality.direction(), notSneaking);

        Block block = PlacementUtils.waterlogged(state);
        block = DripstoneBlock.THICKNESS.get(thickness).on(block);
        block = DripstoneBlock.VERTICALITY.get(verticality).on(block);

        return block;
    }

    @Nullable
    private DripstoneBlock.Verticality getTipDirection(Block.Getter instance, Point blockPos, BlockFace clickedFace) {
        if (clickedFace == BlockFace.TOP) {
            Block below = instance.getBlock(blockPos.relative(BlockFace.BOTTOM));
            return below.isSolid() || DripstoneBlock.isDripstoneWithVerticality(below, DripstoneBlock.Verticality.UP) ? DripstoneBlock.Verticality.UP : null;
        } else if (clickedFace == BlockFace.BOTTOM) {
            Block above = instance.getBlock(blockPos.relative(BlockFace.TOP));
            return above.isSolid() || DripstoneBlock.isDripstoneWithVerticality(above, DripstoneBlock.Verticality.DOWN) ? DripstoneBlock.Verticality.DOWN : null;
        }
        return null;
    }
}
