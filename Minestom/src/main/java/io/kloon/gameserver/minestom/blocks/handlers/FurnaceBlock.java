package io.kloon.gameserver.minestom.blocks.handlers;

import com.google.common.collect.Sets;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

import java.util.Set;

public class FurnaceBlock {
    public static final Set<Material> MATERIALS = Sets.newHashSet(
            Material.FURNACE,
            Material.BLAST_FURNACE,
            Material.SMOKER
    );

    public static final Set<Block> BLOCKS = Sets.newHashSet(
            Block.FURNACE,
            Block.BLAST_FURNACE,
            Block.SMOKER
    );

    public static final BooleanProp LIT = new BooleanProp("lit");
}
