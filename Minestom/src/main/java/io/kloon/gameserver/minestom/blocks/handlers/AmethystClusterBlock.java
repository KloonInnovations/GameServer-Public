package io.kloon.gameserver.minestom.blocks.handlers;

import net.minestom.server.instance.block.Block;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AmethystClusterBlock {
    public static final List<Block> BLOCK_LIST = Arrays.asList(
            Block.AMETHYST_CLUSTER,
            Block.SMALL_AMETHYST_BUD,
            Block.MEDIUM_AMETHYST_BUD,
            Block.LARGE_AMETHYST_BUD
    );

    public static final Set<Block> BLOCKS = new HashSet<>(BLOCK_LIST);
}
