package io.kloon.gameserver.minestom.blocks.properties;

import net.minestom.server.instance.block.Block;

import java.util.HashSet;
import java.util.Set;

public class PropertyLookups {
    public static final BooleanProp LIT = new BooleanProp("lit");
    public static final BooleanProp POWERED = new BooleanProp("powered");

    public static final Set<Block> LIT_BLOCKS = new HashSet<>();
    public static final Set<Block> POWERED_BLOCKS = new HashSet<>();
    static {
        for (Block block : Block.values()) {
            if (block.propertyOptions().containsKey(LIT.getKey())) {
                LIT_BLOCKS.add(block);
            }
            if (block.propertyOptions().containsKey(POWERED.getKey())) {
                POWERED_BLOCKS.add(block);
            }
        }
    }
}
