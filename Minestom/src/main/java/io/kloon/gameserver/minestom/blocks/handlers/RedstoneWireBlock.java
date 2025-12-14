package io.kloon.gameserver.minestom.blocks.handlers;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.minestom.blocks.properties.EnumProp;
import io.kloon.gameserver.minestom.blocks.properties.IntProp;
import io.kloon.gameserver.minestom.utils.PointFmt;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.Direction;

import java.util.Arrays;

public class RedstoneWireBlock {
    public static final EnumProp<Connect> EAST = new EnumProp<>("east", Connect.class);
    public static final EnumProp<Connect> NORTH = new EnumProp<>("north", Connect.class);
    public static final EnumProp<Connect> SOUTH = new EnumProp<>("south", Connect.class);
    public static final EnumProp<Connect> WEST = new EnumProp<>("west", Connect.class);
    public static final IntProp POWER = new IntProp("power", 16);

    public static EnumProp<Connect> fromDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
            default -> throw new IllegalStateException(STR."Invalid direction \{direction}");
        };
    }

    public enum Connect {
        NONE,
        SIDE,
        UP
    }

    public static Block getConnectionState(Block.Getter instance, Block block, Point blockPos) {
        int connections = 0;
        Direction latest = null;
        for (Direction direction : Direction.HORIZONTAL) {
            Point relPos = blockPos.add(direction.vec());
            Block relBlock = instance.getBlock(relPos);

            Block upRelBlock = instance.getBlock(relPos.add(0, 1, 0));
            Block downRelBlock = instance.getBlock(relPos.add(0, -1, 0));
            Connect connect;
            if (isWire(upRelBlock)) {
                connect = Connect.UP;
            } else if (isWire(relBlock) || isWire(downRelBlock)) {
                connect = Connect.SIDE;
            } else {
                connect = Connect.NONE;
            }

            if (connect != Connect.NONE) {
                ++connections;
                latest = direction;
            }
            block = fromDirection(direction).get(connect).on(block);
        }

        Block existing = instance.getBlock(blockPos);
        if (connections == 0) {
            if (isWire(existing)) {
                if (isDot(block)) {
                    return getCross();
                } else if (isCross(block)) {
                    return getDot();
                }
            }
            return getCross();
        }

        if (connections == 1) {
            block = fromDirection(latest.opposite()).get(Connect.SIDE).on(block);
        }

        return block;
    }

    public static void updateNeighbors(Instance instance, Block block, Point blockPos) {
        for (Direction direction : Direction.HORIZONTAL) {
            Connect connect = fromDirection(direction).get(block);
            if (connect == Connect.NONE) continue;

            Connect oppositeConnect = Connect.SIDE;

            Point relPos = blockPos.add(direction.vec());
            if (connect == Connect.UP) {
                relPos = relPos.add(0, 1, 0);
            } else {
                Block evenBlock = instance.getBlock(relPos);
                if (!isWire(evenBlock)) {
                    relPos = relPos.add(0, -1, 0);
                    oppositeConnect = Connect.UP;
                }
            }
            Block relBlock = instance.getBlock(relPos);
            if (!isWire(relBlock)) continue;

            relBlock = fromDirection(direction.opposite()).get(oppositeConnect).on(relBlock);
            instance.setBlock(relPos, relBlock);
        }
    }

    public static boolean isWire(Block block) {
        return block.id() == Block.REDSTONE_WIRE.id();
    }

    public static boolean isDot(Block block) {
        return block.id() == Block.REDSTONE_WIRE.id()
               && Arrays.stream(Direction.HORIZONTAL).allMatch(d -> fromDirection(d).get(block) == Connect.NONE);
    }

    public static boolean isCross(Block block) {
        return block.id() == Block.REDSTONE_WIRE.id()
               && Arrays.stream(Direction.HORIZONTAL).allMatch(d -> fromDirection(d).get(block) == Connect.SIDE);
    }

    public static Block getDot() {
        return Block.REDSTONE_WIRE;
    }

    public static Block getCross() {
        Block block = Block.REDSTONE_WIRE;
        for (Direction direction : Direction.HORIZONTAL) {
            block = fromDirection(direction).get(Connect.SIDE).on(block);
        }
        return block;
    }
}
