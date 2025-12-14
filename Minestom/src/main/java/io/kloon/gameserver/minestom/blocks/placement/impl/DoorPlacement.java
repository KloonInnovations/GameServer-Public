package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.family.BlockFamily;
import io.kloon.gameserver.minestom.blocks.handlers.DoorBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.utils.DirectionUtils;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.kloon.gameserver.minestom.blocks.handlers.DoorBlock.Hinge;
import static io.kloon.gameserver.minestom.blocks.handlers.DoorBlock.Half;

public class DoorPlacement extends BlockPlacementRule {
    public DoorPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        if (!(state.instance() instanceof Instance instance)) {
            return state.block();
        }

        Point blockPos = state.placePosition();
        BlockFace clickedFace = state.blockFace();

        FacingXZ facing = FacingXZ.fromLook(state.playerPosition());
        Hinge hinge = calculateHinge(state.cursorPosition(), facing, clickedFace, state.instance(), blockPos);

        Point upPos = blockPos.add(0, 1, 0);
        Block upBlock = state.block().defaultState();
        boolean canReplaceTop = upBlock == Block.AIR || upBlock.registry().isReplaceable();
        if (canReplaceTop) {
            return null;
        }

        upBlock = DoorBlock.HALF.get(Half.UPPER).on(upBlock);
        upBlock = DoorBlock.HINGE.get(hinge).on(upBlock);
        upBlock = DoorBlock.FACING.get(facing).on(upBlock);
        instance.setBlock(upPos, upBlock);

        Block block = state.block();
        block = DoorBlock.HALF.get(Half.LOWER).on(block);
        block = DoorBlock.HINGE.get(hinge).on(block);
        block = DoorBlock.FACING.get(facing).on(block);

        return block;
    }

    public Hinge calculateHinge(Point hit, FacingXZ facing, BlockFace clickedFace, Block.Getter instance, Point blockPos) {
        Direction across = DirectionUtils.clockwise(facing.toDirection());
        Block right = instance.getBlock(blockPos.add(across.vec()));
        Block left = instance.getBlock(blockPos.add(across.opposite().vec()));
        boolean rightAir = right.compare(Block.AIR);
        boolean leftAir = left.compare(Block.AIR);
        if (!rightAir && leftAir) {
            return Hinge.RIGHT;
        } else if (rightAir && !leftAir) {
            return Hinge.LEFT;
        }

        double z = hit.z() - ((int) hit.z());
        double x = hit.x() - ((int) hit.x());

        if (z < 0) {
            z = -z;
        }

        if (x < 0) {
            x = -x;
        }

        Hinge hinge = switch (facing) {
            case EAST -> z < 0.5 ? Hinge.LEFT : Hinge.RIGHT;
            case WEST -> z > 0.5 ? Hinge.LEFT : Hinge.RIGHT;
            case NORTH -> x < 0.5 ? Hinge.LEFT : Hinge.RIGHT;
            case SOUTH -> x > 0.5 ? Hinge.LEFT : Hinge.RIGHT;
        };

        if (clickedFace.toDirection().horizontal() && facing.toBlockFace() != clickedFace.getOppositeFace()) {
            hinge = hinge.opposite();
        }

        return hinge;
    }

    static {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockBreakEvent.class, event -> {
            Block block = event.getBlock();
            if (!BlockFamily.Variant.DOOR.contains(block)) {
                return;
            }

            Player player = event.getPlayer();
            if (player instanceof CreativePlayer cp && !cp.canEditWorld()) {
                return;
            }

            Half half = DoorBlock.HALF.get(block);
            Direction dir = half == Half.LOWER ? Direction.UP : Direction.DOWN;

            Point otherHalfPos = event.getBlockPosition().add(dir.vec());
            event.getInstance().setBlock(otherHalfPos, Block.AIR);
        });
    }
}
