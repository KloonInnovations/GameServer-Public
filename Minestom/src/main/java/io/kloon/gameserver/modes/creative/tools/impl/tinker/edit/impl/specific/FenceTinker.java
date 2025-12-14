package io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.specific;

import io.kloon.gameserver.minestom.blocks.handlers.FenceBlock;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.utils.DirectionUtils;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.TinkerEditHandler;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;

public class FenceTinker implements TinkerEditHandler {
    @Override
    public Block edit(BlockVec blockPos, Vec cursorPos, Vec raycastEntry, Block block) {
        Direction direction = DirectionUtils.closestXZ(cursorPos.sub(0.5));

        BooleanProp property = FenceBlock.fromDirection(direction);
        return property.invertedOn(block);
    }
}
