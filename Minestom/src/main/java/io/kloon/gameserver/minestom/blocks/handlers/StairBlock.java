package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.family.BlockFamily;
import io.kloon.gameserver.minestom.blocks.properties.BlockProp;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.FacingXZ;
import io.kloon.gameserver.minestom.blocks.properties.enums.StairHalf;
import io.kloon.gameserver.minestom.blocks.properties.enums.StairShape;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;

import java.util.Set;

public class StairBlock {
    public static final EnumProp<FacingXZ> FACING = BlockProp.FACING_XZ;
    public static final EnumProp<StairHalf> HALF = new EnumProp<>("half", StairHalf.class);
    public static final EnumProp<StairShape> SHAPE = new EnumProp<>("shape", StairShape.class);
    public static final BooleanProp WATERLOGGED = WaterBlock.WATERLOGGED;

    public static final Set<Block> BLOCKS = BlockFamily.getBlocksOfVariant(BlockFamily.Variant.STAIRS);

    public static StairShape getShape(Block block, Block.Getter instance, Point blockPos) {
        FacingXZ facing = StairBlock.FACING.get(block);
        BlockFace face = facing.toBlockFace();
        Point facingPos = blockPos.relative(face);
        Block facingBlock = instance.getBlock(facingPos);
        if (isStairs(facingBlock) && StairBlock.HALF.get(facingBlock) == StairBlock.HALF.get(block)) {
            FacingXZ faceOfFacing = StairBlock.FACING.get(facingBlock);
            if (faceOfFacing.axis() != facing.axis() && canTakeShape(block, instance, blockPos, faceOfFacing.opposite())) {
                return faceOfFacing == facing.rotateCounterClockwise()
                        ? StairShape.OUTER_LEFT
                        : StairShape.OUTER_RIGHT;
            }
        }

        Point oppositePos = blockPos.relative(face.getOppositeFace());
        Block oppositeBlock = instance.getBlock(oppositePos);
        if (isStairs(oppositeBlock) && StairBlock.HALF.get(oppositeBlock) == StairBlock.HALF.get(block)) {
            FacingXZ faceOfOpposite = StairBlock.FACING.get(oppositeBlock);
            if (faceOfOpposite.axis() != facing.axis() && canTakeShape(block, instance, blockPos, faceOfOpposite)) {
                return faceOfOpposite == facing.rotateCounterClockwise()
                        ? StairShape.INNER_LEFT
                        : StairShape.INNER_RIGHT;
            }
        }

        return StairShape.STRAIGHT;
    }

    public static boolean isStairs(Block block) {
        return StairBlock.BLOCKS.contains(block.defaultState());
    }

    private static boolean canTakeShape(Block block, Block.Getter instance, Point pos, FacingXZ facing) {
        Block other = instance.getBlock(pos.relative(facing.toBlockFace()));
        if (!StairBlock.isStairs(other)) {
            return true;
        }
        return !StairBlock.isStairs(other)
               || StairBlock.FACING.get(block) != StairBlock.FACING.get(other)
               || StairBlock.HALF.get(block) != StairBlock.HALF.get(other);
    }
}
