package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.enums.AttachFace;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.blocks.transforms.BlockTransformer;
import io.kloon.gameserver.minestom.blocks.transforms.FlipTransform;
import io.kloon.gameserver.minestom.blocks.transforms.RotationTransform;
import io.kloon.gameserver.util.coordinates.Axis;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.Direction;
import net.minestom.server.utils.MathUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AttachedFacingXZBlock extends FacingXZBlock {
    public static final EnumProp<AttachFace> ATTACH_FACE = new EnumProp<>("face", AttachFace.class);

    public static class Placement extends BlockPlacementRule {
        public Placement(@NotNull Block block) {
            super(block);
        }

        @Override
        public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
            Block block = state.block();
            BlockFace face = state.blockFace();
            Pos playerPos = state.playerPosition();

            Direction playerDir = MathUtils.getHorizontalDirection(playerPos.yaw());

            if (face == BlockFace.TOP) {
                block = ATTACH_FACE.get(AttachFace.FLOOR).on(block);

                FacingXZ facing = FacingXZ.fromDirection(playerDir);
                block = FACING_XZ.get(facing).on(block);
            } else if (face == BlockFace.BOTTOM) {
                block = ATTACH_FACE.get(AttachFace.CEILING).on(block);

                FacingXZ facing = FacingXZ.fromDirection(playerDir);
                block = FACING_XZ.get(facing).on(block);
            } else {
                block = ATTACH_FACE.get(AttachFace.WALL).on(block);

                FacingXZ facing = FacingXZ.fromDirection(face.toDirection());
                block = FACING_XZ.get(facing).on(block);
            }

            return block;
        }
    }
}
