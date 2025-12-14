package io.kloon.gameserver.minestom.blocks.placement.impl;

import io.kloon.gameserver.minestom.blocks.family.BlockFamily;
import io.kloon.gameserver.minestom.blocks.handlers.SlabBlock;
import io.kloon.gameserver.minestom.blocks.properties.enums.SlabType;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlabPlacement extends BlockPlacementRule {
    public SlabPlacement(@NotNull Block block) {
        super(block);
    }

    @Override
    public @Nullable Block blockPlace(@NotNull BlockPlacementRule.PlacementState state) {
        Point blockPos = state.placePosition();
        Block block = state.block();

        Block existing = state.instance().getBlock(blockPos);
        if (existing.id() == this.block.id()) {
            block = SlabBlock.TYPE.get(SlabType.DOUBLE).on(block);
            block = SlabBlock.WATERLOGGED.get(false).on(block);
            return block;
        }

        BlockFace face = state.blockFace();
        Point clickPos = state.cursorPosition();
        if (face == BlockFace.BOTTOM || face != BlockFace.TOP && clickPos.y() > 0.5) {
            block = SlabBlock.TYPE.get(SlabType.TOP).on(block);
        }
        return block;
    }

    @Override
    public boolean isSelfReplaceable(@NotNull BlockPlacementRule.Replacement replacement) {
        Material inHand = replacement.material();
        Block inHandBlock = inHand.block();
        boolean holdingSlab = BlockFamily.getBlocksOfVariant(BlockFamily.Variant.SLAB).contains(inHandBlock);

        Block block = replacement.block();
        SlabType slabType = SlabBlock.TYPE.get(block);
        if (slabType == SlabType.DOUBLE || !holdingSlab) {
            return false;
        }

        BlockFace face = replacement.blockFace();
        if (face == BlockFace.BOTTOM) {
            return slabType == SlabType.TOP;
        }
        if (face == BlockFace.TOP) {
            return slabType == SlabType.BOTTOM;
        }
        return false;
    }
}
