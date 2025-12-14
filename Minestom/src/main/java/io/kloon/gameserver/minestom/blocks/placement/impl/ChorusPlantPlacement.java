package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.PipeBlock;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChorusPlantPlacement extends BlockPlacementRule {
    public ChorusPlantPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = state.block();
        Point blockPos = state.placePosition();

        boolean anyConnected = false;
        for (BlockFace face : BlockFace.values()) {
            Point relPos = blockPos.relative(face);
            Block relBlock = state.instance().getBlock(relPos);
            if (block.compare(relBlock)) {
                anyConnected = true;
                block = PipeBlock.getProperty(face).get(true).on(block);

                if (state.instance() instanceof Instance instance) {
                    relBlock = PipeBlock.getProperty(face.getOppositeFace()).get(true).on(relBlock);
                    instance.setBlock(relPos, relBlock, false);
                }
            }
        }

        if (!anyConnected) {
            return PipeBlock.getProperty(state.blockFace().getOppositeFace()).get(true).on(block);
        }

        return block;
    }
}
