package io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.specific;

import io.kloon.gameserver.minestom.blocks.handlers.ChiseledBookshelfBlock;
import io.kloon.gameserver.minestom.blocks.handlers.FacingXZBlock;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.TinkerEditHandler;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import org.joml.Matrix4f;

import static io.kloon.gameserver.util.joml.JomlUtils.threef;
import static io.kloon.gameserver.util.joml.JomlUtils.unthreef;

public class ChiseledBookshelfTinker implements TinkerEditHandler {
    @Override
    public Block edit(BlockVec blockPos, Vec cursorPos, Vec raycastEntry, Block block) {
        FacingXZ facing = FacingXZBlock.FACING_XZ.get(block);
        FacingXZ clicked = FacingXZ.fromCursorPos(cursorPos);
        if (facing != clicked) {
            return block;
        }

        Matrix4f matrix = new Matrix4f().rotateY((float) -facing.radTo(FacingXZ.WEST));
        Vec cursor = unthreef(matrix.transformPosition(threef(cursorPos)));

        double x = cursor.z() < 0 ? cursor.z() + 1 : cursor.z();
        double y = cursor.y();

        BooleanProp prop = computeProp(x, y);

        return prop.invertedOn(block);
    }

    private BooleanProp computeProp(double x, double y) {
        boolean up = y >= 0.5;
        if (x < 1.0 / 3) {
            return up ? ChiseledBookshelfBlock.SLOT_0 : ChiseledBookshelfBlock.SLOT_3;
        } else if (x >= 1.0 / 3 && x < 2.0 / 3) {
            return up ? ChiseledBookshelfBlock.SLOT_1 : ChiseledBookshelfBlock.SLOT_4;
        } else {
            return up ? ChiseledBookshelfBlock.SLOT_2 : ChiseledBookshelfBlock.SLOT_5;
        }
    }
}
