package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.StringBlock;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringPlacement extends BlockPlacementRule {
    public StringPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = state.block();
        Point blockPos = state.placePosition();
        for (Direction direction : Direction.HORIZONTAL) {
            Point relPos = blockPos.add(direction.vec());
            Block relBlock = state.instance().getBlock(relPos);
            if (block.id() == relBlock.id()) {
                BooleanProp prop = StringBlock.fromDirection(direction);
                block = prop.get(true).on(block);

                if (state.instance() instanceof Instance instance) {
                    relBlock = StringBlock.fromDirection(direction.opposite()).get(true).on(relBlock);
                    instance.setBlock(relPos, relBlock, false);
                }
            }
        }
        return block;
    }
}
