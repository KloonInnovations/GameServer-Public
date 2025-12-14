package io.kloon.gameserver.minestom.blocks.family;

import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WoodFamily {
    public static final Map<Block, WoodFamily> FAMILY_BY_BLOCK = new HashMap<>();
    private static final Set<WoodFamily> FAMILIES = new HashSet<>();

    private static final Map<Variant, Set<Block>> BLOCKS_OF_VARIANT = new HashMap<>();

    public static Set<Block> getBlocksOfVariant(Variant variant) {
        return BLOCKS_OF_VARIANT.get(variant);
    }

    public static Set<Block> getBlocksOfVariants(Variant variant, Variant... more) {
        Set<Block> blocks = getBlocksOfVariant(variant);
        for (Variant other : more) {
            blocks.addAll(getBlocksOfVariant(other));
        }
        return blocks;
    }

    public static Set<WoodFamily> getAll() {
        return FAMILIES;
    }

    private final BlockFamily family;
    private final Map<Variant, Block> blocks = new HashMap<>();

    public WoodFamily(BlockFamily family) {
        this.family = family;
        FAMILIES.add(this);
    }

    private WoodFamily put(Variant variant, Block block) {
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
    public Block wood() {
        return blocks.get(Variant.WOOD);
    }

    @Nullable
    public Block strippedWood() {
        return blocks.get(Variant.STRIPPED_WOOD);
    }

    @Nullable
    public Block log() {
        return blocks.get(Variant.LOG);
    }

    @Nullable
    public Block strippedLog() {
        return blocks.get(Variant.STRIPPED_LOG);
    }

    @Nullable
    public Block planks() {
        return blocks.get(Variant.PLANKS);
    }

    @Nullable
    public BlockFamily family() {
        return family;
    }

    public enum Variant {
        WOOD,
        STRIPPED_WOOD,
        LOG,
        STRIPPED_LOG,
        PLANKS,
    }

    public static final WoodFamily OAK = new WoodFamily(BlockFamily.OAK_PLANKS)
            .put(Variant.WOOD, Block.OAK_WOOD)
            .put(Variant.STRIPPED_WOOD, Block.STRIPPED_OAK_WOOD)
            .put(Variant.LOG, Block.OAK_LOG)
            .put(Variant.STRIPPED_LOG, Block.STRIPPED_OAK_LOG)
            .put(Variant.PLANKS, Block.OAK_PLANKS);

    public static final WoodFamily SPRUCE = new WoodFamily(BlockFamily.SPRUCE_PLANKS)
            .put(Variant.WOOD, Block.SPRUCE_WOOD)
            .put(Variant.STRIPPED_WOOD, Block.STRIPPED_SPRUCE_WOOD)
            .put(Variant.LOG, Block.SPRUCE_LOG)
            .put(Variant.STRIPPED_LOG, Block.STRIPPED_SPRUCE_LOG)
            .put(Variant.PLANKS, Block.SPRUCE_PLANKS);

    public static final WoodFamily BIRCH = new WoodFamily(BlockFamily.BIRCH_PLANKS)
            .put(Variant.WOOD, Block.BIRCH_WOOD)
            .put(Variant.STRIPPED_WOOD, Block.STRIPPED_BIRCH_WOOD)
            .put(Variant.LOG, Block.BIRCH_LOG)
            .put(Variant.STRIPPED_LOG, Block.STRIPPED_BIRCH_LOG)
            .put(Variant.PLANKS, Block.BIRCH_PLANKS);

    public static final WoodFamily JUNGLE = new WoodFamily(BlockFamily.JUNGLE_PLANKS)
            .put(Variant.WOOD, Block.JUNGLE_WOOD)
            .put(Variant.STRIPPED_WOOD, Block.STRIPPED_JUNGLE_WOOD)
            .put(Variant.LOG, Block.BIRCH_LOG)
            .put(Variant.STRIPPED_LOG, Block.STRIPPED_JUNGLE_LOG)
            .put(Variant.PLANKS, Block.JUNGLE_PLANKS);

    public static final WoodFamily ACACIA = new WoodFamily(BlockFamily.ACACIA_PLANKS)
            .put(Variant.WOOD, Block.ACACIA_WOOD)
            .put(Variant.STRIPPED_WOOD, Block.STRIPPED_ACACIA_WOOD)
            .put(Variant.LOG, Block.ACACIA_LOG)
            .put(Variant.STRIPPED_LOG, Block.STRIPPED_ACACIA_LOG)
            .put(Variant.PLANKS, Block.ACACIA_PLANKS);

    public static final WoodFamily DARK_OAK = new WoodFamily(BlockFamily.DARK_OAK_PLANKS)
            .put(Variant.WOOD, Block.DARK_OAK_WOOD)
            .put(Variant.STRIPPED_WOOD, Block.STRIPPED_DARK_OAK_WOOD)
            .put(Variant.LOG, Block.DARK_OAK_LOG)
            .put(Variant.STRIPPED_LOG, Block.STRIPPED_DARK_OAK_LOG)
            .put(Variant.PLANKS, Block.DARK_OAK_PLANKS);

    public static final WoodFamily MANGROVE = new WoodFamily(BlockFamily.MANGROVE_PLANKS)
            .put(Variant.WOOD, Block.MANGROVE_WOOD)
            .put(Variant.STRIPPED_WOOD, Block.STRIPPED_MANGROVE_WOOD)
            .put(Variant.LOG, Block.MANGROVE_LOG)
            .put(Variant.STRIPPED_LOG, Block.STRIPPED_MANGROVE_LOG)
            .put(Variant.PLANKS, Block.MANGROVE_PLANKS);

    public static final WoodFamily CHERRY = new WoodFamily(BlockFamily.CHERRY_PLANKS)
            .put(Variant.WOOD, Block.CHERRY_WOOD)
            .put(Variant.STRIPPED_WOOD, Block.STRIPPED_CHERRY_WOOD)
            .put(Variant.LOG, Block.CHERRY_LOG)
            .put(Variant.STRIPPED_LOG, Block.STRIPPED_CHERRY_LOG)
            .put(Variant.PLANKS, Block.CHERRY_PLANKS);
}
