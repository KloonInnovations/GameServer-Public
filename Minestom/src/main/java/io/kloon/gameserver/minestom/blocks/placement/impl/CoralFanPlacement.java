package io.kloon.gameserver.minestom.blocks.placement.impl;

import com.google.common.collect.ImmutableBiMap;
import io.kloon.gameserver.minestom.blocks.handlers.WaterBlock;
import io.kloon.gameserver.minestom.blocks.properties.BlockProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CoralFanPlacement extends BlockPlacementRule {
    public CoralFanPlacement(@NotNull Block block) {
        super(block);
    }

    public static final ImmutableBiMap<Block, Block> BLOCK_TO_WALL = ImmutableBiMap.<Block, Block>builder()
            .put(Block.TUBE_CORAL_FAN, Block.TUBE_CORAL_WALL_FAN)
            .put(Block.BRAIN_CORAL_FAN, Block.BRAIN_CORAL_WALL_FAN)
            .put(Block.BUBBLE_CORAL_FAN, Block.BUBBLE_CORAL_WALL_FAN)
            .put(Block.FIRE_CORAL_FAN, Block.FIRE_CORAL_WALL_FAN)
            .put(Block.HORN_CORAL_FAN, Block.HORN_CORAL_WALL_FAN)
            .put(Block.DEAD_TUBE_CORAL_FAN, Block.DEAD_TUBE_CORAL_WALL_FAN)
            .put(Block.DEAD_BRAIN_CORAL_FAN, Block.DEAD_BRAIN_CORAL_WALL_FAN)
            .put(Block.DEAD_BUBBLE_CORAL_FAN, Block.DEAD_BUBBLE_CORAL_WALL_FAN)
            .put(Block.DEAD_FIRE_CORAL_FAN, Block.DEAD_FIRE_CORAL_WALL_FAN)
            .put(Block.DEAD_HORN_CORAL_FAN, Block.DEAD_HORN_CORAL_WALL_FAN)
            .build();

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = PlacementUtils.waterlogged(state);
        boolean waterlogged = WaterBlock.WATERLOGGED.is(block);

        BlockFace face = state.blockFace();

        if (face == BlockFace.TOP || face == BlockFace.BOTTOM || face == null) {
            return block;
        }

        FacingXZ facing = FacingXZ.fromBlockFace(face);

        block = BLOCK_TO_WALL.get(block.defaultState());
        block = WaterBlock.WATERLOGGED.get(waterlogged).on(block);
        block = BlockProp.FACING_XZ.get(facing).on(block);

        return block;
    }
}
