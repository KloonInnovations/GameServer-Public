package io.kloon.gameserver.minestom.blocks.handlers;

import com.google.common.collect.Sets;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import net.minestom.server.instance.block.Block;

import java.util.Set;

public class LanternBlock {
    public static final BooleanProp HANGING = new BooleanProp("hanging");
    public static final BooleanProp WATERLOGGED = WaterBlock.WATERLOGGED;

    public static final Set<Block> BLOCKS = Sets.newHashSet(Block.LANTERN, Block.SOUL_LANTERN);
}
