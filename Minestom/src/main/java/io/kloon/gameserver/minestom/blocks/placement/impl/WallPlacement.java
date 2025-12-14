package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.FenceBlock;
import io.kloon.gameserver.minestom.blocks.handlers.FenceGateBlock;
import io.kloon.gameserver.minestom.blocks.handlers.WallBlock;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.WallHeight;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.Direction;
import net.minestom.server.utils.block.BlockUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WallPlacement extends BlockPlacementRule {
    public WallPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @NotNull Block blockUpdate(@NotNull BlockPlacementRule.UpdateState state) {
        return getState(state.currentBlock(), state.instance(), state.blockPosition());
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = PlacementUtils.waterlogged(state);
        return getState(block, state.instance(), state.placePosition());
    }

    public Block getState(Block block, Block.Getter instance, Point blockPos) {
        BlockUtils selfBlock = new BlockUtils(instance, blockPos);

        Block upBlock = instance.getBlock(blockPos.relative(BlockFace.TOP));

        for (Direction direction : WallBlock.DIRECTIONALS) {
            Point relPos = blockPos.add(direction.vec());
            Block relBlock = instance.getBlock(relPos);
            boolean connects = shouldConnect(relBlock, direction);
            WallHeight side = getWallHeight(connects, direction, selfBlock);
            EnumProp<WallHeight> prop = WallBlock.getDirectional(direction);
            block = prop.get(side).on(block);
        }

        block = WallBlock.UP.get(needUp(block, upBlock)).on(block);

        return block;
    }

    public WallHeight getWallHeight(boolean connected, Direction direction, BlockUtils selfBlock) {
        if (!connected) {
            return WallHeight.NONE;
        }
        BlockUtils upBlockUtils = selfBlock.above();
        Block upBlock = upBlockUtils.getBlock();
        if (upBlock.isSolid() && !isWall(upBlock)) {
            return WallHeight.TALL;
        }
        if (isWall(upBlock)) {
            return WallBlock.getDirectional(direction, upBlock) == WallHeight.NONE
                    ? WallHeight.LOW
                    : WallHeight.TALL;
        }
        return WallHeight.LOW;
    }

    public boolean shouldConnect(Block block, Direction direction) {
        if (isFenceGate(block)) {
            return FenceGateBlock.isParallel(block, direction);
        }
        return isWall(block)
               || FenceBlock.canAttach(block.defaultState())
               || isPane(block);
    }

    public boolean needUp(Block block, Block up) {
        if (isWall(up) && WallBlock.UP.is(up)) {
            return true;
        }

        int connected = (int) WallBlock.DIRECTIONALS.stream()
                .map(dir -> WallBlock.getDirectional(dir, block))
                .filter(side -> side != WallHeight.NONE)
                .count();

        if (connected == 0 || connected == 1 || connected == 3) {
            return true;
        }

        if (connected == 2 || connected == 4) {
            if (connected == 2) {
                boolean east = WallBlock.getDirectional(Direction.EAST, block) != WallHeight.NONE;
                boolean west = WallBlock.getDirectional(Direction.WEST, block) != WallHeight.NONE;
                boolean north = WallBlock.getDirectional(Direction.NORTH, block) != WallHeight.NONE;
                boolean south = WallBlock.getDirectional(Direction.SOUTH, block) != WallHeight.NONE;

                return !(east == west) || !(north == south);
            }
            return isWall(up) ? WallBlock.UP.is(up) : up.isSolid();
        }

        return true;
    }



    public boolean isFenceGate(Block block) {
        return FenceGateBlock.BLOCKS.contains(block.defaultState());
    }

    public boolean isWall(Block block) {
        return WallBlock.BLOCKS.contains(block.defaultState());
    }

    public boolean isPane(Block block) {
        return block.name().contains("pane") || block == Block.IRON_BARS;
    }
}
