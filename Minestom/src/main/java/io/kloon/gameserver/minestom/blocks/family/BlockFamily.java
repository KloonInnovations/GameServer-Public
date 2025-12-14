package io.kloon.gameserver.minestom.blocks.family;

import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class BlockFamily {
    private static final Set<BlockFamily> FAMILIES = new HashSet<>();
    private static final Map<Block, BlockFamily> FAMILY_BY_BLOCK = new HashMap<>();

    private static final Map<Variant, Set<BlockFamily>> FAMILIES_WITH_VARIANT = new HashMap<>();
    private static final Map<Variant, Set<Block>> BLOCKS_OF_VARIANT = new HashMap<>();

    public static Set<BlockFamily> getFamiliesWithVariant(Variant variant) {
        return FAMILIES_WITH_VARIANT.computeIfAbsent(variant, v -> FAMILY_BY_BLOCK.values().stream()
                .filter(f -> f.get(v) != null)
                .collect(Collectors.toSet()));
    }

    public static Set<Block> getBlocksOfVariant(Variant variant) {
        return BLOCKS_OF_VARIANT.get(variant);
    }

    public static BlockFamily getFamily(Block block) {
        return FAMILY_BY_BLOCK.get(block.defaultState());
    }

    public static Set<BlockFamily> getAll() {
        return FAMILIES;
    }

    private final Map<Variant, Block> blocks = new HashMap<>();

    private BlockFamily(Block block) {
        put(Variant.BLOCK, block);
        FAMILIES.add(this);
    }

    private BlockFamily put(Variant variant, Block block) {
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
        return blocks.get(Variant.BLOCK);
    }

    @Nullable
    public Block button() {
        return blocks.get(Variant.BUTTON);
    }

    @Nullable
    public Block fence() {
        return blocks.get(Variant.FENCE);
    }

    @Nullable
    public Block fenceGate() {
        return blocks.get(Variant.FENCE_GATE);
    }

    @Nullable
    public Block pressurePlate() {
        return blocks.get(Variant.PRESSURE_PLATE);
    }

    @Nullable
    public Block sign() {
        return blocks.get(Variant.SIGN);
    }

    @Nullable
    public Block wallSign() {
        return blocks.get(Variant.WALL_SIGN);
    }

    @Nullable
    public Block hangingSign() {
        return blocks.get(Variant.HANGING_SIGN);
    }

    @Nullable
    public Block wallHangingSign() {
        return blocks.get(Variant.WALL_HANGING_SIGN);
    }

    @Nullable
    public Block slab() {
        return blocks.get(Variant.SLAB);
    }

    @Nullable
    public Block stairs() {
        return blocks.get(Variant.STAIRS);
    }

    @Nullable
    public Block door() {
        return blocks.get(Variant.DOOR);
    }

    @Nullable
    public Block trapdoor() {
        return blocks.get(Variant.TRAPDOOR);
    }

    public enum Variant {
        BLOCK,
        BUTTON,
        CUT,
        CRACKED,
        POLISHED,
        CHISELED,
        FENCE,
        FENCE_GATE,
        PRESSURE_PLATE,
        SIGN,
        WALL_SIGN,
        HANGING_SIGN,
        WALL_HANGING_SIGN,
        SLAB,
        STAIRS,
        DOOR,
        TRAPDOOR,
        MOSAIC,
        WALL,
        ;

        public boolean contains(Block block) {
            return BLOCKS_OF_VARIANT.get(this).contains(block.defaultState());
        }

        public Set<Block> getBlocks() {
            return getBlocksOfVariant(this);
        }
    }

    public static final BlockFamily ACACIA_PLANKS = new BlockFamily(Block.ACACIA_PLANKS)
            .put(Variant.BUTTON, Block.ACACIA_BUTTON)
            .put(Variant.FENCE, Block.ACACIA_FENCE)
            .put(Variant.FENCE_GATE, Block.ACACIA_FENCE_GATE)
            .put(Variant.PRESSURE_PLATE, Block.ACACIA_PRESSURE_PLATE)
            .put(Variant.SIGN, Block.ACACIA_SIGN)
            .put(Variant.WALL_SIGN, Block.ACACIA_WALL_SIGN)
            .put(Variant.HANGING_SIGN, Block.ACACIA_HANGING_SIGN)
            .put(Variant.WALL_HANGING_SIGN, Block.ACACIA_WALL_HANGING_SIGN)
            .put(Variant.SLAB,  Block.ACACIA_SLAB)
            .put(Variant.STAIRS, Block.ACACIA_STAIRS)
            .put(Variant.DOOR, Block.ACACIA_DOOR)
            .put(Variant.TRAPDOOR, Block.ACACIA_TRAPDOOR);

    public static final BlockFamily CHERRY_PLANKS = new BlockFamily(Block.CHERRY_PLANKS)
            .put(Variant.BUTTON, Block.CHERRY_BUTTON)
            .put(Variant.FENCE, Block.CHERRY_FENCE)
            .put(Variant.FENCE_GATE, Block.CHERRY_FENCE_GATE)
            .put(Variant.PRESSURE_PLATE, Block.CHERRY_PRESSURE_PLATE)
            .put(Variant.SIGN, Block.CHERRY_SIGN)
            .put(Variant.WALL_SIGN, Block.CHERRY_WALL_SIGN)
            .put(Variant.HANGING_SIGN, Block.CHERRY_HANGING_SIGN)
            .put(Variant.WALL_HANGING_SIGN, Block.CHERRY_WALL_HANGING_SIGN)
            .put(Variant.SLAB,  Block.CHERRY_SLAB)
            .put(Variant.STAIRS, Block.CHERRY_STAIRS)
            .put(Variant.DOOR, Block.CHERRY_DOOR)
            .put(Variant.TRAPDOOR, Block.CHERRY_TRAPDOOR);

    public static final BlockFamily BIRCH_PLANKS = new BlockFamily(Block.BIRCH_PLANKS)
            .put(Variant.BUTTON, Block.BIRCH_BUTTON)
            .put(Variant.FENCE, Block.BIRCH_FENCE)
            .put(Variant.FENCE_GATE, Block.BIRCH_FENCE_GATE)
            .put(Variant.PRESSURE_PLATE, Block.BIRCH_PRESSURE_PLATE)
            .put(Variant.SIGN, Block.BIRCH_SIGN)
            .put(Variant.WALL_SIGN, Block.BIRCH_WALL_SIGN)
            .put(Variant.HANGING_SIGN, Block.BIRCH_HANGING_SIGN)
            .put(Variant.WALL_HANGING_SIGN, Block.BIRCH_WALL_HANGING_SIGN)
            .put(Variant.SLAB,  Block.BIRCH_SLAB)
            .put(Variant.STAIRS, Block.BIRCH_STAIRS)
            .put(Variant.DOOR, Block.BIRCH_DOOR)
            .put(Variant.TRAPDOOR, Block.BIRCH_TRAPDOOR);

    public static final BlockFamily CRIMSON_PLANKS = new BlockFamily(Block.CRIMSON_PLANKS)
            .put(Variant.BUTTON, Block.CRIMSON_BUTTON)
            .put(Variant.FENCE, Block.CRIMSON_FENCE)
            .put(Variant.FENCE_GATE, Block.CRIMSON_FENCE_GATE)
            .put(Variant.PRESSURE_PLATE, Block.CRIMSON_PRESSURE_PLATE)
            .put(Variant.SIGN, Block.CRIMSON_SIGN)
            .put(Variant.WALL_SIGN, Block.CRIMSON_WALL_SIGN)
            .put(Variant.HANGING_SIGN, Block.CRIMSON_HANGING_SIGN)
            .put(Variant.WALL_HANGING_SIGN, Block.CRIMSON_WALL_HANGING_SIGN)
            .put(Variant.SLAB,  Block.CRIMSON_SLAB)
            .put(Variant.STAIRS, Block.CRIMSON_STAIRS)
            .put(Variant.DOOR, Block.CRIMSON_DOOR)
            .put(Variant.TRAPDOOR, Block.CRIMSON_TRAPDOOR);

    public static final BlockFamily JUNGLE_PLANKS = new BlockFamily(Block.JUNGLE_PLANKS)
            .put(Variant.BUTTON, Block.JUNGLE_BUTTON)
            .put(Variant.FENCE, Block.JUNGLE_FENCE)
            .put(Variant.FENCE_GATE, Block.JUNGLE_FENCE_GATE)
            .put(Variant.PRESSURE_PLATE, Block.JUNGLE_PRESSURE_PLATE)
            .put(Variant.SIGN, Block.JUNGLE_SIGN)
            .put(Variant.WALL_SIGN, Block.JUNGLE_WALL_SIGN)
            .put(Variant.HANGING_SIGN, Block.JUNGLE_HANGING_SIGN)
            .put(Variant.WALL_HANGING_SIGN, Block.JUNGLE_WALL_HANGING_SIGN)
            .put(Variant.SLAB,  Block.JUNGLE_SLAB)
            .put(Variant.STAIRS, Block.JUNGLE_STAIRS)
            .put(Variant.DOOR, Block.JUNGLE_DOOR)
            .put(Variant.TRAPDOOR, Block.JUNGLE_TRAPDOOR);

    public static final BlockFamily OAK_PLANKS = new BlockFamily(Block.OAK_PLANKS)
            .put(Variant.BUTTON, Block.OAK_BUTTON)
            .put(Variant.FENCE, Block.OAK_FENCE)
            .put(Variant.FENCE_GATE, Block.OAK_FENCE_GATE)
            .put(Variant.PRESSURE_PLATE, Block.OAK_PRESSURE_PLATE)
            .put(Variant.SIGN, Block.OAK_SIGN)
            .put(Variant.WALL_SIGN, Block.OAK_WALL_SIGN)
            .put(Variant.HANGING_SIGN, Block.OAK_HANGING_SIGN)
            .put(Variant.WALL_HANGING_SIGN, Block.OAK_WALL_HANGING_SIGN)
            .put(Variant.SLAB,  Block.OAK_SLAB)
            .put(Variant.STAIRS, Block.OAK_STAIRS)
            .put(Variant.DOOR, Block.OAK_DOOR)
            .put(Variant.TRAPDOOR, Block.OAK_TRAPDOOR);

    public static final BlockFamily DARK_OAK_PLANKS = new BlockFamily(Block.DARK_OAK_PLANKS)
            .put(Variant.BUTTON, Block.DARK_OAK_BUTTON)
            .put(Variant.FENCE, Block.DARK_OAK_FENCE)
            .put(Variant.FENCE_GATE, Block.DARK_OAK_FENCE_GATE)
            .put(Variant.PRESSURE_PLATE, Block.DARK_OAK_PRESSURE_PLATE)
            .put(Variant.SIGN, Block.DARK_OAK_SIGN)
            .put(Variant.WALL_SIGN, Block.DARK_OAK_WALL_SIGN)
            .put(Variant.HANGING_SIGN, Block.DARK_OAK_HANGING_SIGN)
            .put(Variant.WALL_HANGING_SIGN, Block.DARK_OAK_WALL_HANGING_SIGN)
            .put(Variant.SLAB,  Block.DARK_OAK_SLAB)
            .put(Variant.STAIRS, Block.DARK_OAK_STAIRS)
            .put(Variant.DOOR, Block.DARK_OAK_DOOR)
            .put(Variant.TRAPDOOR, Block.DARK_OAK_TRAPDOOR);

    public static final BlockFamily SPRUCE_PLANKS = new BlockFamily(Block.SPRUCE_PLANKS)
            .put(Variant.BUTTON, Block.SPRUCE_BUTTON)
            .put(Variant.FENCE, Block.SPRUCE_FENCE)
            .put(Variant.FENCE_GATE, Block.SPRUCE_FENCE_GATE)
            .put(Variant.PRESSURE_PLATE, Block.SPRUCE_PRESSURE_PLATE)
            .put(Variant.SIGN, Block.SPRUCE_SIGN)
            .put(Variant.WALL_SIGN, Block.SPRUCE_WALL_SIGN)
            .put(Variant.HANGING_SIGN, Block.SPRUCE_HANGING_SIGN)
            .put(Variant.WALL_HANGING_SIGN, Block.SPRUCE_WALL_HANGING_SIGN)
            .put(Variant.SLAB,  Block.SPRUCE_SLAB)
            .put(Variant.STAIRS, Block.SPRUCE_STAIRS)
            .put(Variant.DOOR, Block.SPRUCE_DOOR)
            .put(Variant.TRAPDOOR, Block.SPRUCE_TRAPDOOR);

    public static final BlockFamily WARPED_PLANKS = new BlockFamily(Block.WARPED_PLANKS)
            .put(Variant.BUTTON, Block.WARPED_BUTTON)
            .put(Variant.FENCE, Block.WARPED_FENCE)
            .put(Variant.FENCE_GATE, Block.WARPED_FENCE_GATE)
            .put(Variant.PRESSURE_PLATE, Block.WARPED_PRESSURE_PLATE)
            .put(Variant.SIGN, Block.WARPED_SIGN)
            .put(Variant.WALL_SIGN, Block.WARPED_WALL_SIGN)
            .put(Variant.HANGING_SIGN, Block.WARPED_HANGING_SIGN)
            .put(Variant.WALL_HANGING_SIGN, Block.WARPED_WALL_HANGING_SIGN)
            .put(Variant.SLAB,  Block.WARPED_SLAB)
            .put(Variant.STAIRS, Block.WARPED_STAIRS)
            .put(Variant.DOOR, Block.WARPED_DOOR)
            .put(Variant.TRAPDOOR, Block.WARPED_TRAPDOOR);

    public static final BlockFamily MANGROVE_PLANKS = new BlockFamily(Block.MANGROVE_PLANKS)
            .put(Variant.BUTTON, Block.MANGROVE_BUTTON)
            .put(Variant.SLAB,  Block.MANGROVE_SLAB)
            .put(Variant.STAIRS, Block.MANGROVE_STAIRS)
            .put(Variant.FENCE, Block.MANGROVE_FENCE)
            .put(Variant.FENCE_GATE, Block.MANGROVE_FENCE_GATE)
            .put(Variant.PRESSURE_PLATE, Block.MANGROVE_PRESSURE_PLATE)
            .put(Variant.SIGN, Block.MANGROVE_SIGN)
            .put(Variant.WALL_SIGN, Block.MANGROVE_WALL_SIGN)
            .put(Variant.HANGING_SIGN, Block.MANGROVE_HANGING_SIGN)
            .put(Variant.WALL_HANGING_SIGN, Block.MANGROVE_WALL_HANGING_SIGN)
            .put(Variant.DOOR, Block.MANGROVE_DOOR)
            .put(Variant.TRAPDOOR, Block.MANGROVE_TRAPDOOR);

    public static final BlockFamily BAMBOO_PLANKS = new BlockFamily(Block.BAMBOO_PLANKS)
            .put(Variant.BUTTON, Block.BAMBOO_BUTTON)
            .put(Variant.SLAB,  Block.BAMBOO_SLAB)
            .put(Variant.STAIRS, Block.BAMBOO_STAIRS)
            .put(Variant.FENCE, Block.BAMBOO_FENCE)
            .put(Variant.FENCE_GATE, Block.BAMBOO_FENCE_GATE)
            .put(Variant.PRESSURE_PLATE, Block.BAMBOO_PRESSURE_PLATE)
            .put(Variant.SIGN, Block.BAMBOO_SIGN)
            .put(Variant.WALL_SIGN, Block.BAMBOO_WALL_SIGN)
            .put(Variant.HANGING_SIGN, Block.BAMBOO_HANGING_SIGN)
            .put(Variant.WALL_HANGING_SIGN, Block.BAMBOO_WALL_HANGING_SIGN)
            .put(Variant.DOOR, Block.BAMBOO_DOOR)
            .put(Variant.TRAPDOOR, Block.BAMBOO_TRAPDOOR)
            .put(Variant.MOSAIC, Block.BAMBOO_MOSAIC);

    public static final BlockFamily BAMBOO_MOSAIC = new BlockFamily(Block.BAMBOO_MOSAIC)
            .put(Variant.SLAB,  Block.BAMBOO_MOSAIC_SLAB)
            .put(Variant.STAIRS, Block.BAMBOO_MOSAIC_STAIRS);

    public static final BlockFamily MUD_BRICKS = new BlockFamily(Block.MUD_BRICKS)
            .put(Variant.WALL, Block.MUD_BRICK_WALL)
            .put(Variant.STAIRS, Block.MUD_BRICK_STAIRS)
            .put(Variant.SLAB,  Block.MUD_BRICK_SLAB);

    public static final BlockFamily ANDESITE = new BlockFamily(Block.ANDESITE)
            .put(Variant.WALL, Block.ANDESITE_WALL)
            .put(Variant.STAIRS, Block.ANDESITE_STAIRS)
            .put(Variant.SLAB,  Block.ANDESITE_SLAB)
            .put(Variant.POLISHED, Block.POLISHED_ANDESITE);

    public static final BlockFamily POLISHED_ANDESITE = new BlockFamily(Block.POLISHED_ANDESITE)
            .put(Variant.STAIRS, Block.POLISHED_ANDESITE_STAIRS)
            .put(Variant.SLAB,  Block.POLISHED_ANDESITE_SLAB);

    public static final BlockFamily BLACKSTONE = new BlockFamily(Block.BLACKSTONE)
            .put(Variant.WALL, Block.BLACKSTONE_WALL)
            .put(Variant.STAIRS, Block.BLACKSTONE_STAIRS)
            .put(Variant.SLAB,  Block.BLACKSTONE_SLAB)
            .put(Variant.POLISHED, Block.POLISHED_BLACKSTONE);

    public static final BlockFamily POLISHED_BLACKSTONE = new BlockFamily(Block.POLISHED_BLACKSTONE)
            .put(Variant.WALL, Block.POLISHED_BLACKSTONE_WALL)
            .put(Variant.PRESSURE_PLATE, Block.POLISHED_BLACKSTONE_PRESSURE_PLATE)
            .put(Variant.BUTTON, Block.POLISHED_BLACKSTONE_BUTTON)
            .put(Variant.STAIRS, Block.POLISHED_BLACKSTONE_STAIRS)
            .put(Variant.SLAB,  Block.POLISHED_BLACKSTONE_SLAB)
            .put(Variant.POLISHED, Block.POLISHED_BLACKSTONE_BRICKS)
            .put(Variant.CHISELED, Block.CHISELED_POLISHED_BLACKSTONE);

    public static final BlockFamily POLISHED_BLACKSTONE_BRICKS = new BlockFamily(Block.POLISHED_BLACKSTONE_BRICKS)
            .put(Variant.WALL, Block.POLISHED_BLACKSTONE_BRICK_WALL)
            .put(Variant.STAIRS, Block.POLISHED_BLACKSTONE_BRICK_STAIRS)
            .put(Variant.SLAB,  Block.POLISHED_BLACKSTONE_BRICK_SLAB)
            .put(Variant.CRACKED, Block.CRACKED_POLISHED_BLACKSTONE_BRICKS);

    public static final BlockFamily BRICKS = new BlockFamily(Block.BRICKS)
            .put(Variant.WALL, Block.BRICK_WALL)
            .put(Variant.STAIRS, Block.BRICK_STAIRS)
            .put(Variant.SLAB,  Block.BRICK_SLAB);

    public static final BlockFamily END_STONE_BRICKS = new BlockFamily(Block.END_STONE_BRICKS)
            .put(Variant.WALL, Block.END_STONE_BRICK_WALL)
            .put(Variant.STAIRS, Block.END_STONE_BRICK_STAIRS)
            .put(Variant.SLAB,  Block.END_STONE_BRICK_SLAB);

    public static final BlockFamily MOSSY_STONE_BRICKS = new BlockFamily(Block.MOSSY_STONE_BRICKS)
            .put(Variant.WALL, Block.MOSSY_STONE_BRICK_WALL)
            .put(Variant.STAIRS, Block.MOSSY_STONE_BRICK_STAIRS)
            .put(Variant.SLAB,  Block.MOSSY_STONE_BRICK_SLAB);

    public static final BlockFamily COPPER_BLOCK = new BlockFamily(Block.COPPER_BLOCK)
            .put(Variant.CUT, Block.CUT_COPPER)
            .put(Variant.DOOR, Block.COPPER_DOOR)
            .put(Variant.TRAPDOOR, Block.COPPER_TRAPDOOR);

    public static final BlockFamily CUT_COPPER = new BlockFamily(Block.CUT_COPPER)
            .put(Variant.SLAB,  Block.CUT_COPPER_SLAB)
            .put(Variant.STAIRS, Block.CUT_COPPER_STAIRS)
            .put(Variant.CHISELED, Block.CHISELED_COPPER);

    public static final BlockFamily WAXED_COPPER_BLOCK = new BlockFamily(Block.WAXED_COPPER_BLOCK)
            .put(Variant.CUT, Block.WAXED_CUT_COPPER)
            .put(Variant.DOOR, Block.WAXED_COPPER_DOOR)
            .put(Variant.TRAPDOOR, Block.WAXED_COPPER_TRAPDOOR);

    public static final BlockFamily WAXED_CUT_COPPER = new BlockFamily(Block.WAXED_CUT_COPPER)
            .put(Variant.SLAB,  Block.WAXED_CUT_COPPER_SLAB)
            .put(Variant.STAIRS, Block.WAXED_CUT_COPPER_STAIRS)
            .put(Variant.CHISELED, Block.WAXED_CHISELED_COPPER);

    public static final BlockFamily EXPOSED_COPPER = new BlockFamily(Block.EXPOSED_COPPER)
            .put(Variant.CUT, Block.EXPOSED_CUT_COPPER)
            .put(Variant.DOOR, Block.EXPOSED_COPPER_DOOR)
            .put(Variant.TRAPDOOR, Block.EXPOSED_COPPER_TRAPDOOR);

    public static final BlockFamily EXPOSED_CUT_COPPER = new BlockFamily(Block.EXPOSED_CUT_COPPER)
            .put(Variant.SLAB,  Block.EXPOSED_CUT_COPPER_SLAB)
            .put(Variant.STAIRS, Block.EXPOSED_CUT_COPPER_STAIRS)
            .put(Variant.CHISELED, Block.EXPOSED_CHISELED_COPPER);

    public static final BlockFamily WAXED_EXPOSED_COPPER = new BlockFamily(Block.WAXED_EXPOSED_COPPER)
            .put(Variant.CUT, Block.WAXED_EXPOSED_CUT_COPPER)
            .put(Variant.DOOR, Block.WAXED_EXPOSED_COPPER_DOOR)
            .put(Variant.TRAPDOOR, Block.WAXED_EXPOSED_COPPER_TRAPDOOR);

    public static final BlockFamily WAXED_EXPOSED_CUT_COPPER = new BlockFamily(Block.WAXED_EXPOSED_CUT_COPPER)
            .put(Variant.SLAB,  Block.WAXED_EXPOSED_CUT_COPPER_SLAB)
            .put(Variant.STAIRS, Block.WAXED_EXPOSED_CUT_COPPER_STAIRS)
            .put(Variant.CHISELED, Block.WAXED_EXPOSED_CHISELED_COPPER);

    public static final BlockFamily WEATHERED_COPPER = new BlockFamily(Block.WEATHERED_COPPER)
            .put(Variant.CUT, Block.WEATHERED_CUT_COPPER)
            .put(Variant.DOOR, Block.WEATHERED_COPPER_DOOR)
            .put(Variant.TRAPDOOR, Block.WEATHERED_COPPER_TRAPDOOR);

    public static final BlockFamily WEATHERED_CUT_COPPER = new BlockFamily(Block.WEATHERED_CUT_COPPER)
            .put(Variant.SLAB,  Block.WEATHERED_CUT_COPPER_SLAB)
            .put(Variant.STAIRS, Block.WEATHERED_CUT_COPPER_STAIRS)
            .put(Variant.CHISELED, Block.WEATHERED_CHISELED_COPPER);

    public static final BlockFamily WAXED_WEATHERED_COPPER = new BlockFamily(Block.WAXED_WEATHERED_COPPER)
            .put(Variant.CUT, Block.WAXED_WEATHERED_CUT_COPPER)
            .put(Variant.DOOR, Block.WAXED_WEATHERED_COPPER_DOOR)
            .put(Variant.TRAPDOOR, Block.WAXED_WEATHERED_COPPER_TRAPDOOR);

    public static final BlockFamily WAXED_WEATHERED_CUT_COPPER = new BlockFamily(Block.WAXED_WEATHERED_CUT_COPPER)
            .put(Variant.SLAB,  Block.WAXED_WEATHERED_CUT_COPPER_SLAB)
            .put(Variant.STAIRS, Block.WAXED_WEATHERED_CUT_COPPER_STAIRS)
            .put(Variant.CHISELED, Block.WAXED_WEATHERED_CHISELED_COPPER);

    public static final BlockFamily OXIDIZED_COPPER = new BlockFamily(Block.OXIDIZED_COPPER)
            .put(Variant.CUT, Block.OXIDIZED_CUT_COPPER)
            .put(Variant.DOOR, Block.OXIDIZED_COPPER_DOOR)
            .put(Variant.TRAPDOOR, Block.OXIDIZED_COPPER_TRAPDOOR);

    public static final BlockFamily OXIDIZED_CUT_COPPER = new BlockFamily(Block.OXIDIZED_CUT_COPPER)
            .put(Variant.SLAB,  Block.OXIDIZED_CUT_COPPER_SLAB)
            .put(Variant.STAIRS, Block.OXIDIZED_CUT_COPPER_STAIRS)
            .put(Variant.CHISELED, Block.OXIDIZED_CHISELED_COPPER);

    public static final BlockFamily WAXED_OXIDIZED_COPPER = new BlockFamily(Block.WAXED_OXIDIZED_COPPER)
            .put(Variant.CUT, Block.WAXED_OXIDIZED_CUT_COPPER)
            .put(Variant.DOOR, Block.WAXED_OXIDIZED_COPPER_DOOR)
            .put(Variant.TRAPDOOR, Block.WAXED_OXIDIZED_COPPER_TRAPDOOR);

    public static final BlockFamily WAXED_OXIDIZED_CUT_COPPER = new BlockFamily(Block.WAXED_OXIDIZED_CUT_COPPER)
            .put(Variant.SLAB,  Block.WAXED_OXIDIZED_CUT_COPPER_SLAB)
            .put(Variant.STAIRS, Block.WAXED_OXIDIZED_CUT_COPPER_STAIRS)
            .put(Variant.CHISELED, Block.WAXED_OXIDIZED_CHISELED_COPPER);

    public static final BlockFamily COBBLESTONE = new BlockFamily(Block.COBBLESTONE)
            .put(Variant.WALL, Block.COBBLESTONE_WALL)
            .put(Variant.STAIRS, Block.COBBLESTONE_STAIRS)
            .put(Variant.SLAB,  Block.COBBLESTONE_SLAB);

    public static final BlockFamily MOSSY_COBBLESTONE = new BlockFamily(Block.MOSSY_COBBLESTONE)
            .put(Variant.WALL, Block.MOSSY_COBBLESTONE_WALL)
            .put(Variant.STAIRS, Block.MOSSY_COBBLESTONE_STAIRS)
            .put(Variant.SLAB,  Block.MOSSY_COBBLESTONE_SLAB);

    public static final BlockFamily DIORITE = new BlockFamily(Block.DIORITE)
            .put(Variant.WALL, Block.DIORITE_WALL)
            .put(Variant.STAIRS, Block.DIORITE_STAIRS)
            .put(Variant.SLAB,  Block.DIORITE_SLAB)
            .put(Variant.POLISHED, Block.POLISHED_DIORITE);

    public static final BlockFamily POLISHED_DIORITE = new BlockFamily(Block.POLISHED_DIORITE)
            .put(Variant.STAIRS, Block.POLISHED_DIORITE_STAIRS)
            .put(Variant.SLAB,  Block.POLISHED_DIORITE_SLAB);

    public static final BlockFamily GRANITE = new BlockFamily(Block.GRANITE)
            .put(Variant.WALL, Block.GRANITE_WALL)
            .put(Variant.STAIRS, Block.GRANITE_STAIRS)
            .put(Variant.SLAB,  Block.GRANITE_SLAB)
            .put(Variant.POLISHED, Block.POLISHED_GRANITE);

    public static final BlockFamily POLISHED_GRANITE = new BlockFamily(Block.POLISHED_GRANITE)
            .put(Variant.STAIRS, Block.POLISHED_GRANITE_STAIRS)
            .put(Variant.SLAB,  Block.POLISHED_GRANITE_SLAB);

    public static final BlockFamily IRON = new BlockFamily(Block.IRON_BLOCK)
            .put(Variant.DOOR, Block.IRON_DOOR)
            .put(Variant.TRAPDOOR, Block.IRON_TRAPDOOR);

    public static final BlockFamily TUFF = new BlockFamily(Block.TUFF)
            .put(Variant.WALL, Block.TUFF_WALL)
            .put(Variant.STAIRS, Block.TUFF_STAIRS)
            .put(Variant.SLAB,  Block.TUFF_SLAB)
            .put(Variant.CHISELED, Block.CHISELED_TUFF)
            .put(Variant.POLISHED, Block.POLISHED_TUFF);

    public static final BlockFamily POLISHED_TUFF = new BlockFamily(Block.POLISHED_TUFF)
            .put(Variant.WALL, Block.POLISHED_TUFF_WALL)
            .put(Variant.STAIRS, Block.POLISHED_TUFF_STAIRS)
            .put(Variant.SLAB,  Block.POLISHED_TUFF_SLAB)
            .put(Variant.POLISHED, Block.TUFF_BRICKS);

    public static final BlockFamily TUFF_BRICKS = new BlockFamily(Block.TUFF_BRICKS)
            .put(Variant.WALL, Block.TUFF_BRICK_WALL)
            .put(Variant.STAIRS, Block.TUFF_BRICK_STAIRS)
            .put(Variant.SLAB,  Block.TUFF_BRICK_SLAB)
            .put(Variant.CHISELED, Block.CHISELED_TUFF_BRICKS);

    public static final BlockFamily NETHER_BRICKS = new BlockFamily(Block.NETHER_BRICKS)
            .put(Variant.FENCE, Block.NETHER_BRICK_FENCE)
            .put(Variant.WALL, Block.NETHER_BRICK_WALL)
            .put(Variant.STAIRS, Block.NETHER_BRICK_STAIRS)
            .put(Variant.SLAB,  Block.NETHER_BRICK_SLAB)
            .put(Variant.CHISELED, Block.CHISELED_NETHER_BRICKS)
            .put(Variant.CRACKED, Block.CRACKED_NETHER_BRICKS);

    public static final BlockFamily RED_NETHER_BRICKS = new BlockFamily(Block.RED_NETHER_BRICKS)
            .put(Variant.SLAB,  Block.RED_NETHER_BRICK_SLAB)
            .put(Variant.STAIRS, Block.RED_NETHER_BRICK_STAIRS)
            .put(Variant.WALL, Block.RED_NETHER_BRICK_WALL);

    public static final BlockFamily PRISMARINE = new BlockFamily(Block.PRISMARINE)
            .put(Variant.WALL, Block.PRISMARINE_WALL)
            .put(Variant.STAIRS, Block.PRISMARINE_STAIRS)
            .put(Variant.SLAB,  Block.PRISMARINE_SLAB);

    public static final BlockFamily PURPUR = new BlockFamily(Block.PURPUR_BLOCK)
            .put(Variant.STAIRS, Block.PURPUR_STAIRS)
            .put(Variant.SLAB,  Block.PURPUR_SLAB);

    public static final BlockFamily PRISMARINE_BRICKS = new BlockFamily(Block.PRISMARINE_BRICKS)
            .put(Variant.STAIRS, Block.PRISMARINE_BRICK_STAIRS)
            .put(Variant.SLAB,  Block.PRISMARINE_BRICK_SLAB);

    public static final BlockFamily DARK_PRISMARINE = new BlockFamily(Block.DARK_PRISMARINE)
            .put(Variant.STAIRS, Block.DARK_PRISMARINE_STAIRS)
            .put(Variant.SLAB,  Block.DARK_PRISMARINE_SLAB);

    public static final BlockFamily QUARTZ = new BlockFamily(Block.QUARTZ_BLOCK)
            .put(Variant.STAIRS, Block.QUARTZ_STAIRS)
            .put(Variant.SLAB,  Block.QUARTZ_SLAB)
            .put(Variant.CHISELED, Block.CHISELED_QUARTZ_BLOCK);

    public static final BlockFamily SMOOTH_QUARTZ = new BlockFamily(Block.SMOOTH_QUARTZ)
            .put(Variant.STAIRS, Block.SMOOTH_QUARTZ_STAIRS)
            .put(Variant.SLAB,  Block.SMOOTH_QUARTZ_SLAB);

    public static final BlockFamily SANDSTONE = new BlockFamily(Block.SANDSTONE)
            .put(Variant.WALL, Block.SANDSTONE_WALL)
            .put(Variant.STAIRS, Block.SANDSTONE_STAIRS)
            .put(Variant.SLAB,  Block.SANDSTONE_SLAB)
            .put(Variant.CHISELED, Block.CHISELED_SANDSTONE)
            .put(Variant.CUT, Block.CUT_SANDSTONE);

    public static final BlockFamily CUT_SANDSTONE = new BlockFamily(Block.CUT_SANDSTONE)
            .put(Variant.SLAB,  Block.CUT_SANDSTONE_SLAB);

    public static final BlockFamily SMOOTH_SANDSTONE = new BlockFamily(Block.SMOOTH_SANDSTONE)
            .put(Variant.SLAB,  Block.SMOOTH_SANDSTONE_SLAB)
            .put(Variant.STAIRS, Block.SMOOTH_SANDSTONE_STAIRS);

    public static final BlockFamily RED_SANDSTONE = new BlockFamily(Block.RED_SANDSTONE)
            .put(Variant.WALL, Block.RED_SANDSTONE_WALL)
            .put(Variant.STAIRS, Block.RED_SANDSTONE_STAIRS)
            .put(Variant.SLAB,  Block.RED_SANDSTONE_SLAB)
            .put(Variant.CHISELED, Block.CHISELED_RED_SANDSTONE)
            .put(Variant.CUT, Block.CUT_RED_SANDSTONE);

    public static final BlockFamily CUT_RED_SANDSTONE = new BlockFamily(Block.CUT_RED_SANDSTONE)
            .put(Variant.SLAB,  Block.CUT_RED_SANDSTONE_SLAB);

    public static final BlockFamily SMOOTH_RED_SANDSTONE = new BlockFamily(Block.SMOOTH_RED_SANDSTONE)
            .put(Variant.SLAB,  Block.SMOOTH_RED_SANDSTONE_SLAB)
            .put(Variant.STAIRS, Block.SMOOTH_RED_SANDSTONE_STAIRS);

    public static final BlockFamily STONE = new BlockFamily(Block.STONE)
            .put(Variant.SLAB,  Block.STONE_SLAB)
            .put(Variant.PRESSURE_PLATE, Block.STONE_PRESSURE_PLATE)
            .put(Variant.BUTTON, Block.STONE_BUTTON)
            .put(Variant.STAIRS, Block.STONE_STAIRS);

    public static final BlockFamily STONE_BRICK = new BlockFamily(Block.STONE_BRICKS)
            .put(Variant.WALL, Block.STONE_BRICK_WALL)
            .put(Variant.STAIRS, Block.STONE_BRICK_STAIRS)
            .put(Variant.SLAB,  Block.STONE_BRICK_SLAB)
            .put(Variant.CHISELED, Block.CHISELED_STONE_BRICKS)
            .put(Variant.CRACKED, Block.CRACKED_STONE_BRICKS);

    public static final BlockFamily COBBLED_DEEPSLATE = new BlockFamily(Block.COBBLED_DEEPSLATE)
            .put(Variant.SLAB,  Block.COBBLED_DEEPSLATE_SLAB)
            .put(Variant.STAIRS, Block.COBBLED_DEEPSLATE_STAIRS)
            .put(Variant.WALL, Block.COBBLED_DEEPSLATE_WALL)
            .put(Variant.CHISELED, Block.CHISELED_DEEPSLATE)
            .put(Variant.POLISHED, Block.POLISHED_DEEPSLATE);

    public static final BlockFamily POLISHED_DEEPSLATE = new BlockFamily(Block.POLISHED_DEEPSLATE)
            .put(Variant.SLAB,  Block.POLISHED_DEEPSLATE_SLAB)
            .put(Variant.STAIRS, Block.POLISHED_DEEPSLATE_STAIRS)
            .put(Variant.WALL, Block.POLISHED_DEEPSLATE_WALL);

    public static final BlockFamily DEEPSLATE_BRICKS = new BlockFamily(Block.DEEPSLATE_BRICKS)
            .put(Variant.SLAB,  Block.DEEPSLATE_BRICK_SLAB)
            .put(Variant.STAIRS, Block.DEEPSLATE_BRICK_STAIRS)
            .put(Variant.WALL, Block.DEEPSLATE_BRICK_WALL)
            .put(Variant.CRACKED, Block.CRACKED_DEEPSLATE_BRICKS);

    public static final BlockFamily DEEPSLATE_TILES = new BlockFamily(Block.DEEPSLATE_TILES)
            .put(Variant.SLAB,  Block.DEEPSLATE_TILE_SLAB)
            .put(Variant.STAIRS, Block.DEEPSLATE_TILE_STAIRS)
            .put(Variant.WALL, Block.DEEPSLATE_TILE_WALL)
            .put(Variant.CRACKED, Block.CRACKED_DEEPSLATE_TILES);
}
