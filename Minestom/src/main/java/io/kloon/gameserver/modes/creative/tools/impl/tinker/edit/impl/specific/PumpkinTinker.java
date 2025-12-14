package io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.specific;

import io.kloon.gameserver.minestom.blocks.handlers.FacingXZBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.TinkerEditHandler;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;

import java.util.Arrays;
import java.util.List;

public class PumpkinTinker implements TinkerEditHandler {
    public static final List<Block> BLOCKS = Arrays.asList(
            Block.PUMPKIN,
            Block.CARVED_PUMPKIN,
            Block.JACK_O_LANTERN
    );

    @Override
    public Block edit(BlockVec blockPos, Vec cursorPos, Vec raycastEntry, Block block) {
        if (block.defaultState() == Block.JACK_O_LANTERN) {
            return Block.PUMPKIN;
        }

        if (block.defaultState() == Block.CARVED_PUMPKIN) {
            return Block.JACK_O_LANTERN.withProperties(block.properties());
        }

        FacingXZ facingXZ = FacingXZ.fromCursorPos(cursorPos);
        return FacingXZBlock.FACING_XZ.get(facingXZ).on(Block.CARVED_PUMPKIN);
    }
}
