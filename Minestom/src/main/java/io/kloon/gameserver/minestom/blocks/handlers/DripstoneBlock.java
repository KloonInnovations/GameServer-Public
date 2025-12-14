package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.utils.DirectionUtils;
import io.kloon.gameserver.util.coordinates.Axis;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;

public class DripstoneBlock {
    public static final EnumProp<Thickness> THICKNESS = new EnumProp<>("thickness", Thickness.class);
    public static final EnumProp<Verticality> VERTICALITY = new EnumProp<>("vertical_direction", Verticality.class);
    public static final BooleanProp WATERLOGGED = WaterBlock.WATERLOGGED;

    public enum Thickness {
        TIP,
        TIP_MERGE,
        FRUSTUM,
        MIDDLE,
        BASE
    }

    public enum Verticality {
        UP,
        DOWN;

        public Verticality opposite() {
            return this == UP ? DOWN : UP;
        }

        public Direction direction() {
            return this == UP ? Direction.UP : Direction.DOWN;
        }

        public static Verticality fromDirection(Direction dir) {
            return switch (dir) {
                case UP -> UP;
                case DOWN -> DOWN;
                default -> throw new IllegalStateException("Invalid direction " + dir);
            };
        }
    }

    public static Thickness computeThickness(Block.Getter instance, Point blockPos, Direction direction, boolean notSneaking) {
        Verticality vert = Verticality.fromDirection(direction);
        Verticality oppositeVert = vert.opposite();

        Block relBlock = instance.getBlock(blockPos.add(direction.vec()));
        Thickness relThickness = THICKNESS.get(relBlock);
        if (isDripstoneWithVerticality(relBlock, oppositeVert)) {
            if (notSneaking || relThickness == Thickness.TIP_MERGE) {
                return Thickness.TIP_MERGE;
            }
            return Thickness.TIP;
        }
        if (!isDripstoneWithVerticality(relBlock, vert)) {
            return Thickness.TIP;
        }
        if (relThickness == Thickness.TIP || relThickness == Thickness.TIP_MERGE) {
            return Thickness.FRUSTUM;
        }
        Block oppositeRelBlock = instance.getBlock(blockPos.add(direction.opposite().vec()));
        return isDripstoneWithVerticality(oppositeRelBlock, vert)
                ? Thickness.MIDDLE
                : Thickness.BASE;
    }

    public static boolean isDripstoneWithVerticality(Block block, Verticality verticality) {
        return block.id() == Block.POINTED_DRIPSTONE.id() && VERTICALITY.get(block) == verticality;
    }

    private static boolean isValidPlacement(Block.Getter instance, Point blockPos, Direction dir) {
        if (DirectionUtils.axis(dir) != Axis.Y) return false;
        Point relPos = blockPos.add(dir.opposite().vec());
        Block relBlock = instance.getBlock(relPos);
        return relBlock.isSolid() || isDripstoneWithVerticality(relBlock, Verticality.fromDirection(dir));
    }
}
