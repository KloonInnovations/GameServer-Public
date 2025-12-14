package io.kloon.gameserver.minestom.blocks.handlers;

import com.google.common.collect.Sets;
import io.kloon.gameserver.minestom.blocks.properties.IntProp;
import net.minestom.server.instance.block.Block;

import java.util.Set;

public class BeehiveBlock extends FacingXZBlock {
    public static final IntProp HONEY_LEVEL = new IntProp("honey_level", 6);

    public static final Set<Block> BLOCKS = Sets.newHashSet(Block.BEE_NEST, Block.BEEHIVE);
}
