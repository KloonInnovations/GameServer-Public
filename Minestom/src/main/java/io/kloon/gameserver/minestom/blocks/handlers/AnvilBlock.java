package io.kloon.gameserver.minestom.blocks.handlers;

import com.google.common.collect.Sets;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

import java.util.Set;

public class AnvilBlock {
    public static final Set<Block> BLOCKS = Sets.newHashSet(
            Block.ANVIL,
            Block.CHIPPED_ANVIL,
            Block.DAMAGED_ANVIL
    );
}
