package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.MultifaceBlock;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultifacePlacement extends BlockPlacementRule {
    private boolean waterlogged = false;

    public MultifacePlacement(@NotNull Block block) {
        super(block);
    }

    public MultifacePlacement waterlogged() {
        this.waterlogged = true;
        return this;
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = state.block();
        Block existing = state.instance().getBlock(state.placePosition());
        if (existing.compare(this.block)) {
            block = existing;
        } else if (waterlogged) {
            block = PlacementUtils.waterlogged(state);
        }

        BlockFace clickedFace = state.blockFace();
        if (clickedFace == null) {
            return block;
        }

        BlockFace connectedFace = clickedFace.getOppositeFace();
        block = MultifaceBlock.fromBlockFace(connectedFace).get(true).on(block);

        return block;
    }

    @Override
    public boolean isSelfReplaceable(@NotNull BlockPlacementRule.Replacement replacement) {
        return true;
    }
}
