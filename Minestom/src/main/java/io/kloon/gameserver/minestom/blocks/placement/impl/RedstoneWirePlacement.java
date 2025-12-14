package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.RedstoneWireBlock;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedstoneWirePlacement extends BlockPlacementRule {
    public RedstoneWirePlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @NotNull Block blockUpdate(@NotNull BlockPlacementRule.UpdateState state) {
        return RedstoneWireBlock.getConnectionState(state.instance(), state.currentBlock(), state.blockPosition());
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = RedstoneWireBlock.getConnectionState(state.instance(), state.block(), state.placePosition());
        if (state.instance() instanceof Instance instance) {
            RedstoneWireBlock.updateNeighbors(instance, block, state.placePosition());
        }
        return block;
    }

    @Override
    public boolean isSelfReplaceable(@NotNull BlockPlacementRule.Replacement replacement) {
        Block block = replacement.block();
        return RedstoneWireBlock.isDot(block) || RedstoneWireBlock.isCross(block);
    }
}
