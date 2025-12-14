package io.kloon.gameserver.minestom.blocks.placement.generic;

import io.kloon.gameserver.minestom.blocks.properties.BlockProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXYZ;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// opposite by default, actually
public class LooksFacingXYZPlacement extends BlockPlacementRule {
    private boolean opposite = false;

    public LooksFacingXYZPlacement(@NotNull Block block) {
        super(block);
    }

    public LooksFacingXYZPlacement opposite() {
        this.opposite = true;
        return this;
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Direction dir = PlacementUtils.getLookingDirection(state).opposite();
        if (opposite) {
            dir = dir.opposite();
        }
        return BlockProp.FACING_XYZ.get(FacingXYZ.fromDirection(dir)).on(state);
    }
}
