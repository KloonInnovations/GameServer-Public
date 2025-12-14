package io.kloon.gameserver.minestom.blocks.placement.impl.sign;

import io.kloon.gameserver.minestom.blocks.family.BlockFamily;
import io.kloon.gameserver.minestom.blocks.handlers.WaterBlock;
import io.kloon.gameserver.minestom.blocks.handlers.signs.StandingSignBlock;
import io.kloon.gameserver.minestom.blocks.properties.BannerRotProp;
import io.kloon.gameserver.minestom.blocks.properties.BlockProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SignPlacement extends BlockPlacementRule {
    public SignPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = PlacementUtils.waterlogged(state);
        boolean inWater = WaterBlock.WATERLOGGED.is(block);

        BlockFace face = state.blockFace();

        if (face == BlockFace.BOTTOM) {
            return null;
        }

        if (face == BlockFace.TOP) {
            int segment = BannerRotProp.convertToSegment(state.playerPosition().yaw() + 180);
            return StandingSignBlock.ROTATION.get(segment).on(block);
        }

        FacingXZ facing = FacingXZ.fromBlockFace(face);

        BlockFamily family = BlockFamily.getFamily(block);
        Block wallBlock = family.wallSign();

        wallBlock = WaterBlock.WATERLOGGED.get(inWater).on(wallBlock);
        wallBlock = BlockProp.FACING_XZ.get(facing).on(wallBlock);

        return wallBlock;
    }
}
