package io.kloon.gameserver.minestom.blocks.placement.generic;

import io.kloon.gameserver.minestom.blocks.handlers.FacingXZBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClickFacingXZPlacement extends BlockPlacementRule {
    private boolean waterlogged = false;
    private boolean opposite = false;

    public ClickFacingXZPlacement(@NotNull Block block) {
        super(block);
    }

    public ClickFacingXZPlacement opposite() {
        this.opposite = true;
        return this;
    }

    public ClickFacingXZPlacement waterlogged() {
        this.waterlogged = true;
        return this;
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = state.block();
        if (waterlogged) {
            block = PlacementUtils.waterlogged(state);
        }

        BlockFace face = state.blockFace();
        if (face == null) {
            return block;
        }
        if (face == BlockFace.BOTTOM || face == BlockFace.TOP) {
            return null;
        }

        if (opposite) {
            face = face.getOppositeFace();
        }

        FacingXZ facing = FacingXZ.fromBlockFace(face);

        block = FacingXZBlock.FACING_XZ.get(facing).on(block);

        return block;
    }
}
