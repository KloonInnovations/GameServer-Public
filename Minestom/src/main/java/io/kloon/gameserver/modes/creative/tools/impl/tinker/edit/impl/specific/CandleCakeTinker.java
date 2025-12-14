package io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.specific;

import io.kloon.gameserver.minestom.blocks.handlers.CakeBlock;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.TinkerEditHandler;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;

public class CandleCakeTinker implements TinkerEditHandler {
    @Override
    public Block edit(BlockVec blockPos, Vec cursorPos, Vec raycastEntry, Block block) {
        if (cursorPos.y() > 0.5) {
            return CakeBlock.LIT.invertedOn(block);
        }
        return block;
    }
}
