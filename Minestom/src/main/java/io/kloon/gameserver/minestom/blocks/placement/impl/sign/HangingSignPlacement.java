package io.kloon.gameserver.minestom.blocks.placement.impl.sign;

import io.kloon.gameserver.minestom.blocks.family.BlockFamily;
import io.kloon.gameserver.minestom.blocks.handlers.WaterBlock;
import io.kloon.gameserver.minestom.blocks.handlers.signs.CeilingHangingSignBlock;
import io.kloon.gameserver.minestom.blocks.properties.BannerRotProp;
import io.kloon.gameserver.minestom.blocks.properties.BlockProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.blocks.utils.PlacementUtils;
import io.kloon.gameserver.minestom.utils.DirectionUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HangingSignPlacement extends BlockPlacementRule {
    public HangingSignPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block block = PlacementUtils.waterlogged(state);
        boolean inWater = WaterBlock.WATERLOGGED.is(block);

        BlockFace face = state.blockFace();

        if (face == BlockFace.TOP) {
            return null;
        }

        if (face == BlockFace.BOTTOM) {
            boolean attached;
            int segment;
            if (state.isPlayerShifting()) {
                attached = true;
                segment = BannerRotProp.convertToSegment(state.playerPosition().yaw() + 180);
            } else {
                attached = false;
                Direction direction = FacingXZ.fromLook(state.playerPosition()).toDirection().opposite();
                segment = BannerRotProp.convertToSegment(direction);
            }
            block = CeilingHangingSignBlock.ATTACHED.get(attached).on(block);
            return CeilingHangingSignBlock.ROTATION.get(segment).on(block);
        }

        Vec normal = face.toDirection().vec();
        Vec cross = normal.cross(new Vec(0, 1, 0));

        Point blockCenter = state.placePosition().add(0.5, 0.5, 0.5);
        Pos playerPos = state.playerPosition().withY(blockCenter.y());
        Vec toPlayer = playerPos.sub(blockCenter).asVec().normalize();
        double dot = cross.dot(toPlayer);

        FacingXZ facing = FacingXZ.fromBlockFace(DirectionUtils.clockwise(face));
        if (dot < 0) {
            facing = facing.opposite();
        }

        BlockFamily family = BlockFamily.getFamily(block);
        Block wallBlock = family.wallHangingSign();

        wallBlock = WaterBlock.WATERLOGGED.get(inWater).on(wallBlock);
        wallBlock = BlockProp.FACING_XZ.get(facing).on(wallBlock);

        return wallBlock;
    }
}
