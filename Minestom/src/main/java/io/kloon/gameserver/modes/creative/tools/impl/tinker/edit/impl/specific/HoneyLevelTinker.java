package io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.specific;

import io.kloon.gameserver.minestom.blocks.handlers.BeehiveBlock;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.TinkerEditHandler;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;

public class HoneyLevelTinker implements TinkerEditHandler {
    @Override
    public Block edit(BlockVec blockPos, Vec cursorPos, Vec raycastEntry, Block block) {
        int honeyLevel = BeehiveBlock.HONEY_LEVEL.get(block);
        honeyLevel = honeyLevel == 5 ? 0 : 5;
        return BeehiveBlock.HONEY_LEVEL.get(honeyLevel).on(block);
    }
}
