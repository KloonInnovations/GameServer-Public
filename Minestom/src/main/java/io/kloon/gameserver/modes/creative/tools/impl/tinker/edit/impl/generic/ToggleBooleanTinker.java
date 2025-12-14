package io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.generic;

import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.TinkerEditHandler;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;

public class ToggleBooleanTinker implements TinkerEditHandler {
    private final BooleanProp property;

    public ToggleBooleanTinker(BooleanProp property) {
        this.property = property;
    }

    @Override
    public Block edit(BlockVec blockPos, Vec cursorPos, Vec raycastEntry, Block block) {
        return property.invertedOn(block);
    }
}
