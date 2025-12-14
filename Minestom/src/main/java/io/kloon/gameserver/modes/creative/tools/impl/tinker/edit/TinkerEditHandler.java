package io.kloon.gameserver.modes.creative.tools.impl.tinker.edit;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;

public interface TinkerEditHandler {
    /*
    cursorPos is all between 0 and 1, sent by the block place packet
    raycastEntry is computed server-side based on player look dir, also the rel pos between 0 and 1
     */
    Block edit(BlockVec blockPos, Vec cursorPos, Vec raycastEntry, Block block);
}
