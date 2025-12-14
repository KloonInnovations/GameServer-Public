package io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.generic;

import io.kloon.gameserver.minestom.blocks.properties.IntProp;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.TinkerEditHandler;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;

public class CycleIntTinker implements TinkerEditHandler {
    private final IntProp property;

    public CycleIntTinker(IntProp property) {
        this.property = property;
    }

    @Override
    public Block edit(BlockVec blockPos, Vec cursorPos, Vec raycastEntry, Block block) {
        int value = property.get(block) + 1;
        if (value >= property.getMaxExcluded()) {
            value = property.getMinIncluded();
        }
        return property.get(value).on(block);
    }
}
