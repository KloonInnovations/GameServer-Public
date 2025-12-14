package io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.specific;

import io.kloon.gameserver.minestom.blocks.handlers.BrewingStandBlock;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.TinkerEditHandler;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.position.PositionUtils;

public class BrewingStandTinker implements TinkerEditHandler {
    @Override
    public Block edit(BlockVec blockPos, Vec cursorPos, Vec raycastEntry, Block block) {
        Vec dir = raycastEntry.sub(0.5).normalize();
        float yaw = PositionUtils.getLookYaw(dir.x(), dir.z()) + 180;
        BooleanProp bottleProp = getProp(yaw);
        return bottleProp.invertedOn(block);
    }

    private BooleanProp getProp(float yaw) {
        if (yaw > 0 && yaw <= 180) {
            return BrewingStandBlock.HAS_BOTTLE_0;
        } else if (yaw > 180 && yaw <= 270) {
            return BrewingStandBlock.HAS_BOTTLE_2;
        }
        return BrewingStandBlock.HAS_BOTTLE_1;
    }
}
