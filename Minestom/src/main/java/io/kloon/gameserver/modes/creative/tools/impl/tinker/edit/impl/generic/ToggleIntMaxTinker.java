package io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.generic;

import io.kloon.gameserver.minestom.blocks.properties.IntProp;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.TinkerEditHandler;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;

public class ToggleIntMaxTinker implements TinkerEditHandler {
    private final IntProp property;

    public ToggleIntMaxTinker(IntProp property) {
        this.property = property;
    }

    @Override
    public Block edit(BlockVec blockPos, Vec cursorPos, Vec raycastEntry, Block block) {
        int value = property.get(block);
        if (value == property.getMaxIncluded()) {
            return property.get(0).on(block);
        }
        return property.get(property.getMaxIncluded()).on(block);
    }
}
