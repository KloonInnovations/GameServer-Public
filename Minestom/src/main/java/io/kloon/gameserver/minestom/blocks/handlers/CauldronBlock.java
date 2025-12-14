package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.IntProp;
import net.minestom.server.instance.block.Block;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CauldronBlock {
    public static final List<Block> BLOCKS = Arrays.asList(
            Block.CAULDRON,
            Block.WATER_CAULDRON,
            Block.LAVA_CAULDRON,
            Block.POWDER_SNOW_CAULDRON
    );

    public static final Set<Block> BLOCKS_SET = new HashSet<>(BLOCKS);

    public static final IntProp LEVEL = new IntProp("level", 1, 4);
}
