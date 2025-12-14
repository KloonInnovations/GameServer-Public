package io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.generic;

import io.kloon.gameserver.chestmenus.listing.cycle.Cycle;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.TinkerEditHandler;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CycleBlocksTinker implements TinkerEditHandler {
    private final List<Block> blocks;

    public CycleBlocksTinker(List<Block> blocks) {
        this.blocks = blocks;
    }

    public CycleBlocksTinker(Block... blocks) {
        this(Arrays.asList(blocks));
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    @Override
    public Block edit(BlockVec blockPos, Vec cursorPos, Vec raycastEntry, Block block) {
        Cycle<Block> cycle = new Cycle<>(blocks);
        cycle.select(block);

        Map<String, String> properties = new HashMap<>(block.properties());
        Block newBlock = cycle.goForward();
        properties.keySet().removeIf(key -> !newBlock.propertyOptions().containsKey(key));

        return newBlock.withProperties(properties);
    }
}
