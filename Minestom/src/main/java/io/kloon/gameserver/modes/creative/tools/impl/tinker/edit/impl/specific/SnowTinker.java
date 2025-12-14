package io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.specific;

import io.kloon.gameserver.minestom.blocks.handlers.SnowLayerBlock;
import io.kloon.gameserver.minestom.blocks.properties.IntProp;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.TinkerEditHandler;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;

public class SnowTinker implements TinkerEditHandler {
    private static final IntProp LAYERS = SnowLayerBlock.LAYERS;

    @Override
    public Block edit(BlockVec blockPos, Vec cursorPos, Vec raycastEntry, Block block) {
        if (block == Block.SNOW_BLOCK) {
            return LAYERS.get(1).on(Block.SNOW);
        }

        int layer = LAYERS.get(block);
        if (layer == LAYERS.getMaxIncluded() - 1) {
            return Block.SNOW_BLOCK;
        } else if (layer == LAYERS.getMaxIncluded()) {
            layer = LAYERS.getMinIncluded();
        } else {
            ++layer;
        }

        return LAYERS.get(layer).on(block);
    }
}
