package io.kloon.gameserver.minestom.blocks.family;

import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CoralFamily {
    private static final Map<Block, CoralFamily> FAMILY_BY_BLOCK = new HashMap<>();

    private static final Map<Variant, Set<Block>> BLOCKS_OF_VARIANT = new HashMap<>();

    public static Set<Block> getBlocksOfVariant(Variant variant) {
        return BLOCKS_OF_VARIANT.get(variant);
    }

    private final Map<Variant, Block> blocks = new HashMap<>();

    private CoralFamily put(Variant variant, Block block) {
        blocks.put(variant, block);
        FAMILY_BY_BLOCK.put(block, this);
        BLOCKS_OF_VARIANT.computeIfAbsent(variant, _ -> new HashSet<>()).add(block);
        return this;
    }

    @Nullable
    public Block get(Variant variant) {
        return blocks.get(variant);
    }

    @Nullable
    public Block block() {
        return get(Variant.BLOCK);
    }

    @Nullable
    public Block deadBlock() {
        return get(Variant.DEAD_BLOCK);
    }

    @Nullable
    public Block coral() {
        return get(Variant.CORAL);
    }

    @Nullable
    public Block deadCoral() {
        return get(Variant.DEAD_CORAL);
    }

    @Nullable
    public Block fan() {
        return get(Variant.FAN);
    }

    @Nullable
    public Block deadFan() {
        return get(Variant.DEAD_FAN);
    }

    public enum Variant {
        BLOCK,
        DEAD_BLOCK,
        CORAL,
        DEAD_CORAL,
        FAN,
        WALL_FAN,
        DEAD_FAN,
        DEAD_WALL_FAN
    }

    public static final CoralFamily TUBE = new CoralFamily()
            .put(Variant.BLOCK, Block.TUBE_CORAL_BLOCK)
            .put(Variant.DEAD_BLOCK, Block.DEAD_TUBE_CORAL_BLOCK)
            .put(Variant.CORAL, Block.TUBE_CORAL)
            .put(Variant.DEAD_CORAL, Block.DEAD_TUBE_CORAL)
            .put(Variant.FAN, Block.TUBE_CORAL_FAN)
            .put(Variant.FAN, Block.TUBE_CORAL_WALL_FAN)
            .put(Variant.DEAD_FAN, Block.DEAD_TUBE_CORAL_FAN)
            .put(Variant.DEAD_WALL_FAN, Block.DEAD_TUBE_CORAL_WALL_FAN);

    public static final CoralFamily BRAIN = new CoralFamily()
            .put(Variant.BLOCK, Block.BRAIN_CORAL_BLOCK)
            .put(Variant.DEAD_BLOCK, Block.DEAD_BRAIN_CORAL_BLOCK)
            .put(Variant.CORAL, Block.BRAIN_CORAL)
            .put(Variant.DEAD_CORAL, Block.DEAD_BRAIN_CORAL)
            .put(Variant.FAN, Block.BRAIN_CORAL_FAN)
            .put(Variant.FAN, Block.BRAIN_CORAL_WALL_FAN)
            .put(Variant.DEAD_FAN, Block.DEAD_BRAIN_CORAL_FAN)
            .put(Variant.DEAD_WALL_FAN, Block.DEAD_BRAIN_CORAL_WALL_FAN);

    public static final CoralFamily BUBBLE = new CoralFamily()
            .put(Variant.BLOCK, Block.BUBBLE_CORAL_BLOCK)
            .put(Variant.DEAD_BLOCK, Block.DEAD_BUBBLE_CORAL_BLOCK)
            .put(Variant.CORAL, Block.BUBBLE_CORAL)
            .put(Variant.DEAD_CORAL, Block.DEAD_BUBBLE_CORAL)
            .put(Variant.FAN, Block.BUBBLE_CORAL_FAN)
            .put(Variant.FAN, Block.BUBBLE_CORAL_WALL_FAN)
            .put(Variant.DEAD_FAN, Block.DEAD_BUBBLE_CORAL_FAN)
            .put(Variant.DEAD_WALL_FAN, Block.DEAD_BUBBLE_CORAL_WALL_FAN);

    public static final CoralFamily FIRE = new CoralFamily()
            .put(Variant.BLOCK, Block.FIRE_CORAL_BLOCK)
            .put(Variant.DEAD_BLOCK, Block.DEAD_FIRE_CORAL_BLOCK)
            .put(Variant.CORAL, Block.FIRE_CORAL)
            .put(Variant.DEAD_CORAL, Block.DEAD_FIRE_CORAL)
            .put(Variant.FAN, Block.FIRE_CORAL_FAN)
            .put(Variant.FAN, Block.FIRE_CORAL_WALL_FAN)
            .put(Variant.DEAD_FAN, Block.DEAD_FIRE_CORAL_FAN)
            .put(Variant.DEAD_WALL_FAN, Block.DEAD_FIRE_CORAL_WALL_FAN);

    public static final CoralFamily HORN = new CoralFamily()
            .put(Variant.BLOCK, Block.HORN_CORAL_BLOCK)
            .put(Variant.DEAD_BLOCK, Block.DEAD_HORN_CORAL_BLOCK)
            .put(Variant.CORAL, Block.HORN_CORAL)
            .put(Variant.DEAD_CORAL, Block.DEAD_HORN_CORAL)
            .put(Variant.FAN, Block.HORN_CORAL_FAN)
            .put(Variant.FAN, Block.HORN_CORAL_WALL_FAN)
            .put(Variant.DEAD_FAN, Block.DEAD_HORN_CORAL_FAN)
            .put(Variant.DEAD_WALL_FAN, Block.DEAD_HORN_CORAL_WALL_FAN);
}
