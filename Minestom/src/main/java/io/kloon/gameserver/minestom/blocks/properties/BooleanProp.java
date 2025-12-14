package io.kloon.gameserver.minestom.blocks.properties;

import net.minestom.server.instance.block.Block;

public class BooleanProp {
    private final String key;

    private final BlockProp trueProp;
    private final BlockProp falseProp;

    public BooleanProp(String key) {
        this.key = key;

        this.trueProp = new BlockProp(key, "true");
        this.falseProp = new BlockProp(key, "false");
    }

    public String getKey() {
        return key;
    }

    public BlockProp get(boolean value) {
        return value ? trueProp : falseProp;
    }

    public boolean is(Block block) {
        return "true".equals(block.getProperty(key));
    }

    public Block invertedOn(Block block) {
        String property = block.getProperty(key);
        if (property == null) return block;
        String inverted = "true".equals(property) ? "false" : "true";
        return block.withProperty(key, inverted);
    }
}
