package io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.impl.specific;

import io.kloon.gameserver.chestmenus.listing.cycle.Cycle;
import io.kloon.gameserver.minestom.blocks.handlers.WallBlock;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.WallHeight;
import io.kloon.gameserver.minestom.utils.DirectionUtils;
import io.kloon.gameserver.modes.creative.tools.impl.tinker.edit.TinkerEditHandler;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;

public class WallTinker implements TinkerEditHandler {
    @Override
    public Block edit(BlockVec blockPos, Vec cursorPos, Vec raycastEntry, Block block) {
        Direction direction = DirectionUtils.closestXZ(cursorPos.sub(0.5));

        EnumProp<WallHeight> property = WallBlock.getDirectional(direction);

        WallHeight wallHeight = property.get(block);
        Cycle<WallHeight> cycle = Cycle.fromEnum(WallHeight.values(), wallHeight);
        wallHeight = cycle.goForward();

        return property.get(wallHeight).on(block);
    }
}
