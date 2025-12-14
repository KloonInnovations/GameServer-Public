package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.CrafterBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FrontAndTop;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrafterPlacement extends BlockPlacementRule {
    public CrafterPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Direction front = PlacementUtils.getLookingDirection(state).opposite();
        Direction top = switch (front) {
            case DOWN -> PlacementUtils.getHorizontalDirection(state).opposite();
            case UP -> PlacementUtils.getHorizontalDirection(state);
            case NORTH, SOUTH, WEST, EAST -> Direction.UP;
        };

        FrontAndTop frontAndTop = FrontAndTop.from(front, top);

        return CrafterBlock.ORIENTATION.get(frontAndTop).on(state.block());
    }
}
