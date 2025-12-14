package io.kloon.gameserver.minestom.blocks.placement.generic;

import io.kloon.gameserver.minestom.blocks.properties.BlockProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXYZ;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClickFacingXYZPlacement extends BlockPlacementRule {
    private boolean waterlogged = false;

    public ClickFacingXYZPlacement(@NotNull Block block) {
        super(block);
    }

    public ClickFacingXYZPlacement waterlogged() {
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

        FacingXYZ facing = FacingXYZ.fromBlockFace(face);

        block = BlockProp.FACING_XYZ.get(facing).on(block);

        return block;
    }
}
