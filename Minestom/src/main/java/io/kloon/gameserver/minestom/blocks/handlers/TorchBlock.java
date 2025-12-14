package io.kloon.gameserver.minestom.blocks.handlers;

import com.google.common.collect.ImmutableBiMap;
import net.minestom.server.instance.block.Block;

import java.util.Set;

public class TorchBlock {
    public static final ImmutableBiMap<Block, Block> TORCH_TO_WALL = ImmutableBiMap.<Block, Block>builder()
            .put(Block.TORCH, Block.WALL_TORCH)
            .put(Block.SOUL_TORCH, Block.SOUL_WALL_TORCH)
            .put(Block.REDSTONE_TORCH, Block.REDSTONE_WALL_TORCH)
            .build();

    public static final Set<Block> BLOCKS = TORCH_TO_WALL.keySet();
}
