package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.CandleBlock;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CandlePlacement extends BlockPlacementRule {
    public CandlePlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = PlacementUtils.waterlogged(state);

        Block existing = state.instance().getBlock(state.placePosition());
        int candles = CandleBlock.CANDLES.getOrZero(existing) + 1;

        return CandleBlock.CANDLES.get(candles).on(block);
    }

    @Override
    public boolean isSelfReplaceable(@NotNull BlockPlacementRule.Replacement replacement) {
        Block block = replacement.block();
        Block blockType = block.defaultState();
        if (blockType == this.block) {
            int candles = CandleBlock.CANDLES.get(block);
            return candles < 4;
        }
        return super.isSelfReplaceable(replacement);
    }
}
