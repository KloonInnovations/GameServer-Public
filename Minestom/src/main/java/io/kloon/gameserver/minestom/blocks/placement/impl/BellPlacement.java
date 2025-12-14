package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.handlers.BellBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.utils.block.BlockUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BellPlacement extends BlockPlacementRule {
    public BellPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Block.Getter instance = state.instance();
        BlockFace blockFace = state.blockFace();
        Point blockPos = state.placePosition();

        Block block = state.block();

        BellBlock.Attachment attachment;
        FacingXZ facing = FacingXZ.fromLook(state.playerPosition());

        if (blockFace == BlockFace.BOTTOM) {
            attachment = BellBlock.Attachment.CEILING;
        } else if (blockFace == BlockFace.TOP) {
            attachment = BellBlock.Attachment.FLOOR;
        } else {
            Block facingBlock = instance.getBlock(blockPos.relative(blockFace));
            Block oppositeBlock = instance.getBlock(blockPos.relative(blockFace.getOppositeFace()));

            if (facingBlock.isSolid() && oppositeBlock.isSolid()) {
                attachment = BellBlock.Attachment.DOUBLE_WALL;
                facing = FacingXZ.fromBlockFace(blockFace).opposite();
            } else if (facingBlock.isSolid() && !oppositeBlock.isSolid()) {
                attachment = BellBlock.Attachment.SINGLE_WALL;
            } else if (oppositeBlock.isSolid() && !facingBlock.isSolid()) {
                attachment = BellBlock.Attachment.SINGLE_WALL;
                facing = FacingXZ.fromBlockFace(blockFace.getOppositeFace());
            } else {
                attachment = BellBlock.Attachment.FLOOR;
            }
        }

        block = BellBlock.FACING.get(facing).on(block);
        block = BellBlock.ATTACHMENT.get(attachment).on(block);

        return block;
    }
}
