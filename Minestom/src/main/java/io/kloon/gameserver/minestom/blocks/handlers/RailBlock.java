package io.kloon.gameserver.minestom.blocks.handlers;

import com.google.common.collect.Sets;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.enums.RailShape;
import io.kloon.gameserver.minestom.utils.BlockFmt;
import io.kloon.gameserver.minestom.utils.PointFmt;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class RailBlock {
    public static final EnumProp<RailShape> RAIL_SHAPE = new EnumProp<>("shape", RailShape.class);

    public static final Set<Block> BLOCKS = Sets.newHashSet(
            Block.RAIL,
            Block.ACTIVATOR_RAIL,
            Block.DETECTOR_RAIL,
            Block.POWERED_RAIL
    );

    public static boolean isRail(Instance instance, Point point) {
        Block block = instance.getBlock(point);
        Block blockType = Block.fromKey(block.name());
        return BLOCKS.contains(blockType);
    }

    public static class RailState {
        private final Instance instance;
        private final Point blockPos;
        private Block block;
        private final boolean isStraight;

        public RailState(Instance instance, Point blockPos, Block block) {
            this.instance = instance;
            this.blockPos = blockPos;
            this.block = block;

            Block blockType = Block.fromKey(block.name());
            this.isStraight = blockType != Block.RAIL;
        }

        public Block getBlock() {
            return block;
        }

        @Nullable
        private RailState getVerticalRailState(Point point) {
            return RailBlock.getVerticalRailState(instance, point);
        }

        public RailState place(RailShape inputShape) {
            return place(false, inputShape, null);
        }

        private RailState place(boolean hasNeighborSignal, RailShape inputShape, @Nullable RailState justConnected) {
            Point northPos = blockPos.relative(BlockFace.NORTH);
            Point southPos = blockPos.relative(BlockFace.SOUTH);
            Point westPos = blockPos.relative(BlockFace.WEST);
            Point eastPos = blockPos.relative(BlockFace.EAST);

            Predicate<Point> isRail = point -> {
                if (justConnected != null && justConnected.blockPos.equals(point)) {
                    return true;
                }
                return RailBlock.isRail(instance, point);
            };

            Predicate<Point> isRailVertical = point -> {
                if (justConnected != null) {
                    Point connPos = justConnected.blockPos;
                    if (connPos.blockX() == point.blockX() && connPos.blockZ() == point.blockZ()) {
                        return true;
                    }
                }
                return getVerticalRailState(point) != null;
            };

            boolean isNorth = isRailVertical.test(northPos);
            boolean isSouth = isRailVertical.test(southPos);
            boolean isWest = isRailVertical.test(westPos);
            boolean isEast = isRailVertical.test(eastPos);

            boolean isZ = isNorth || isSouth;
            boolean isX = isWest || isEast;

            boolean isSouthEast = isSouth && isEast;
            boolean isSouthWest = isSouth && isWest;
            boolean isNorthEast = isNorth && isEast;
            boolean isNorthWest = isNorth && isWest;

            RailShape outputShape = null;
            if (isZ && !isX) {
                outputShape = RailShape.NORTH_SOUTH;
            }
            if (isX && !isZ) {
                outputShape = RailShape.EAST_WEST;
            }

            if (!isStraight) {
                if (isSouthEast && !isNorth && !isWest) {
                    outputShape = RailShape.SOUTH_EAST;
                }
                if (isSouthWest && !isNorth && !isEast) {
                    outputShape = RailShape.SOUTH_WEST;
                }
                if (isNorthWest && !isSouth && !isEast) {
                    outputShape = RailShape.NORTH_WEST;
                }
                if (isNorthEast && !isSouth && !isWest) {
                    outputShape = RailShape.NORTH_EAST;
                }
            }

            if (outputShape == null) {
                if (isZ && isX) {
                    outputShape = inputShape;
                } else if (isZ) {
                    outputShape = RailShape.NORTH_SOUTH;
                } else if (isX) {
                    outputShape = RailShape.EAST_WEST;
                }
                if (!this.isStraight) {
                    if (hasNeighborSignal) {
                        if (isSouthEast) {
                            outputShape = RailShape.SOUTH_EAST;
                        }
                        if (isSouthWest) {
                            outputShape = RailShape.SOUTH_WEST;
                        }
                        if (isNorthEast) {
                            outputShape = RailShape.NORTH_EAST;
                        }
                        if (isNorthWest) {
                            outputShape = RailShape.NORTH_WEST;
                        }
                    } else {
                        if (isNorthWest) {
                            outputShape = RailShape.NORTH_WEST;
                        }
                        if (isNorthEast) {
                            outputShape = RailShape.NORTH_EAST;
                        }
                        if (isSouthWest) {
                            outputShape = RailShape.SOUTH_WEST;
                        }
                        if (isSouthEast) {
                            outputShape = RailShape.SOUTH_EAST;
                        }
                    }
                }
            }

            if (outputShape == RailShape.NORTH_SOUTH) {
                if (isRail.test(northPos.relative(BlockFace.TOP))) {
                    outputShape = RailShape.ASCENDING_NORTH;
                }
                if (isRail.test(southPos.relative(BlockFace.TOP))) {
                    outputShape = RailShape.ASCENDING_SOUTH;
                }
            }
            if (outputShape == RailShape.EAST_WEST) {
                if (isRail.test(eastPos.relative(BlockFace.TOP))) {
                    outputShape = RailShape.ASCENDING_EAST;
                }
                if (isRail.test(westPos.relative(BlockFace.TOP))) {
                    outputShape = RailShape.ASCENDING_WEST;
                }
            }

            if (outputShape == null) {
                outputShape = inputShape;
            }

            this.block = RailBlock.RAIL_SHAPE.get(outputShape).on(block);

            instance.setBlock(blockPos, block);

            if (justConnected == null) {
                for (Point neighbor : computeConnections(outputShape, blockPos)) {
                    RailState connected = RailBlock.getVerticalRailState(instance, neighbor);
                    if (connected == null) continue;
                    RailShape neighborShape = RailBlock.RAIL_SHAPE.get(instance.getBlock(neighbor));
                    connected.place(hasNeighborSignal, neighborShape, this);
                }
            }

            return this;
        }
    }

    private static List<Point> computeConnections(RailShape shape, Point blockPos) {
        List<Point> connections = new ArrayList<>(2);
        for (Vec connectionVec : shape.connectionVecs()) {
            Point connection = blockPos.add(connectionVec);
            connections.add(connection);
        }
        return connections;
    }

    @Nullable
    private static RailState getVerticalRailState(Instance instance, Point point) {
        if (isRail(instance, point)) {
            return new RailState(instance, point, instance.getBlock(point));
        }

        Point upPos = point.relative(BlockFace.TOP);
        if (isRail(instance, upPos)) {
            return new RailState(instance, upPos, instance.getBlock(upPos));
        }

        Point downPos = point.relative(BlockFace.BOTTOM);
        if (isRail(instance, downPos)) {
            return new RailState(instance, downPos, instance.getBlock(downPos));
        }

        return null;
    }
}
