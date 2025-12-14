package io.kloon.gameserver.minestom.blocks.handlers;

import com.google.common.collect.ImmutableBiMap;
import io.kloon.gameserver.minestom.blocks.properties.BlockProp;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.IntProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import net.minestom.server.instance.block.Block;

import java.util.Set;

public class SkullBlock {
    public static final IntProp GROUND_ROTATION = new IntProp("rotation", 16);
    public static final EnumProp<FacingXZ> WALL_FACING = BlockProp.FACING_XZ;

    public static final ImmutableBiMap<Block,Block> BLOCK_TO_WALL = ImmutableBiMap.<Block, Block>builder()
            .put(Block.SKELETON_SKULL, Block.SKELETON_WALL_SKULL)
            .put(Block.ZOMBIE_HEAD, Block.ZOMBIE_WALL_HEAD)
            .put(Block.CREEPER_HEAD, Block.CREEPER_WALL_HEAD)
            .put(Block.PIGLIN_HEAD, Block.PIGLIN_WALL_HEAD)
            .put(Block.WITHER_SKELETON_SKULL, Block.WITHER_SKELETON_WALL_SKULL)
            .put(Block.DRAGON_HEAD, Block.DRAGON_WALL_HEAD)
            .put(Block.PLAYER_HEAD, Block.PLAYER_WALL_HEAD)
            .build();
    public static final Set<Block> BLOCKS = BLOCK_TO_WALL.keySet();
}
