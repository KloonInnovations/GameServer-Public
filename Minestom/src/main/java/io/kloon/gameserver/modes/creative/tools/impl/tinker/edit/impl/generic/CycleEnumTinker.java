package io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.generic;

import io.kloon.gameserver.chestmenus.listing.cycle.Cycle;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.TinkerEditHandler;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;

public class CycleEnumTinker<T extends Enum<T>> implements TinkerEditHandler {
    private final EnumProp<T> prop;

    public CycleEnumTinker(EnumProp<T> prop) {
        this.prop = prop;
    }

    @Override
    public Block edit(BlockVec blockPos, Vec cursorPos, Vec raycastEntry, Block block) {
        Cycle<T> cycle = new Cycle<>(prop.getOptions());

        T value = prop.get(block);
        cycle.select(value);

        return prop.get(cycle.goForward()).on(block);
    }
}
