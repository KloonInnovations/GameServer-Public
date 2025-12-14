package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.KloonPlacementRules;
import io.kloon.gameserver.minestom.blocks.handlers.BedBlock;
import io.kloon.gameserver.minestom.blocks.handlers.FacingXZBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.BedPart;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.Direction;
import net.minestom.server.utils.MathUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BedPlacement extends BlockPlacementRule {
    public BedPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Direction dir = MathUtils.getHorizontalDirection(state.playerPosition().yaw());

        FacingXZ facing = FacingXZ.fromDirection(dir);
        Block foot = FacingXZBlock.FACING_XZ.get(facing).on(state);

        Point footPos = state.placePosition();
        Point headPos = footPos.relative(BlockFace.fromDirection(dir));

        Block currentHeadBlock = state.instance().getBlock(headPos);
        boolean canReplace = currentHeadBlock == Block.AIR || currentHeadBlock.registry().isReplaceable();
        if (!canReplace) {
            return null;
        }

        if (state.instance() instanceof Instance instance) {
            Block head = BedBlock.BED_PART.get(BedPart.HEAD).on(foot);
            head = KloonPlacementRules.injectHandler(head);
            instance.setBlock(headPos, head);
        }

        return foot;
    }

    static {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockBreakEvent.class, event -> {
            Block block = event.getBlock();
            if (!BedBlock.BLOCKS.contains(block.defaultState())) {
                return;
            }

            Player player = event.getPlayer();
            if (player instanceof CreativePlayer cp && !cp.canEditWorld()) {
                return;
            }

            FacingXZ facing = FacingXZBlock.FACING_XZ.get(block);
            BedPart brokenPart = BedBlock.BED_PART.get(block);

            Direction dir = facing.toDirection();
            if (brokenPart == BedPart.HEAD) {
                dir = dir.opposite();
            }

            BlockVec otherPartPos = event.getBlockPosition().relative(BlockFace.fromDirection(dir));
            Block otherPart = event.getInstance().getBlock(otherPartPos);
            if (otherPart.name().equals(block.name())) {
                event.getInstance().setBlock(otherPartPos, Block.AIR);
            }
        });
    }
}
