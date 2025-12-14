package io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.specific;

import io.kloon.gameserver.minestom.blocks.handlers.BambooStalkBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.BambooLeaves;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.TinkerEditHandler;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.generic.CycleEnumTinker;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;

public class BambooStalkTinker implements TinkerEditHandler {
    private final AgeTinker ageTinker = new AgeTinker();
    private final CycleEnumTinker<BambooLeaves> leavesTinker = new CycleEnumTinker<>(BambooStalkBlock.LEAVES);

    @Override
    public Block edit(BlockVec blockPos, Vec cursorPos, Vec raycastEntry, Block block) {
        if (cursorPos.y() == 1) {
            return ageTinker.edit(blockPos, cursorPos, raycastEntry, block);
        }
        return leavesTinker.edit(blockPos, cursorPos, raycastEntry, block);
    }
}
