package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.minestom.blocks.handlers.VineBlock;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.utils.DirectionUtils;
import io.kloon.gameserver.minestom.utils.PointFmt;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VinePlacement extends BlockPlacementRule {
    public VinePlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block existing = state.instance().getBlock(state.placePosition());
        boolean isVine = existing.id() == this.block.id();
        Block block = isVine ? existing : this.block;

        BlockFace face = state.blockFace();
        if (face == BlockFace.BOTTOM || face == null) {
            return block;
        }
        face = face.getOppositeFace();

        Point relPos = state.placePosition().relative(face);
        Block relBlock = state.instance().getBlock(relPos);
        if (relBlock.isSolid()) {
            BooleanProp prop = VineBlock.fromDirection(DirectionUtils.fromBlockFace(face));
            block = prop.get(true).on(block);
        } else if (VineBlock.allSidesAreOff(block)) {
            return null;
        }

        return block;
    }

    @Override
    public boolean isSelfReplaceable(@NotNull BlockPlacementRule.Replacement replacement) {
        return true;
    }
}
