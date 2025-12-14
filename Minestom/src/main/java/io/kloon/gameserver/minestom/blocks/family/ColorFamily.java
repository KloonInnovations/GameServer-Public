package io.kloon.gameserver.minestom.blocks.family;

import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.*;

public class ColorFamily {
    private static final Map<Block, ColorFamily> FAMILY_BY_BLOCK = new HashMap<>();
    private static final Map<Material, ColorFamily> FAMILY_BY_MATERIAL = new HashMap<>();
    private static final Map<Variant, Set<Block>> BLOCKS_OF_VARIANT = new HashMap<>();
    private static final List<ColorFamily> LIST = new ArrayList<>();

    public static List<ColorFamily> getAll() {
        return Collections.unmodifiableList(LIST);
    }

    @UnknownNullability
    public static ColorFamily getFamily(Block block) {
        return FAMILY_BY_BLOCK.get(block);
    }

    @UnknownNullability
    public static ColorFamily getFamily(Material block) {
        return FAMILY_BY_MATERIAL.get(block);
    }

    private final Map<Variant, Block> blocks = new HashMap<>();
    private final Map<Variant, Material> materials = new HashMap<>();

    private ColorFamily(Block wool) {
        put(Variant.WOOL, wool);
        LIST.add(this);
    }

    private ColorFamily put(Variant variant, Block block) {
        blocks.put(variant, block);
        FAMILY_BY_BLOCK.put(block, this);
        BLOCKS_OF_VARIANT.computeIfAbsent(variant, _ -> new HashSet<>()).add(block);

        Material material = block.registry().material();
        if (material != null) {
            materials.put(variant, material);
            FAMILY_BY_MATERIAL.put(material, this);
        }
        return this;
    }

    private ColorFamily put(Variant variant, Material material) {
        materials.put(variant, material);
        FAMILY_BY_MATERIAL.put(material, this);

        Block block = material.block();
        if (block != null) {
            blocks.put(variant, block);
            FAMILY_BY_BLOCK.put(block, this);
        }
        return this;
    }

    @Nullable
    public Block getBlock(Variant variant) {
        return blocks.get(variant);
    }

    @Nullable
    public Material getMaterial(Variant variant) {
        return materials.get(variant);
    }

    public Block woolBlock() {
        return getBlock(Variant.WOOL);
    }

    public Material woolMat() {
        return getMaterial(Variant.WOOL);
    }

    public enum Variant {
        WOOL,
        TERRACOTTA,
        GLAZED_TERRACOTTA,
        CONCRETE,
        CONCRETE_POWDER,
        STAINED_GLASS,
        STAINED_GLASS_PANE,
        BANNER,
        BED,
        CANDLE,
        CANDLE_CAKE,
        CARPET,
        DYE,
        SHULKER_BOX
    }

    public static final ColorFamily WHITE = new ColorFamily(Block.WHITE_WOOL)
            .put(Variant.TERRACOTTA, Block.WHITE_TERRACOTTA)
            .put(Variant.GLAZED_TERRACOTTA, Block.WHITE_GLAZED_TERRACOTTA)
            .put(Variant.CONCRETE, Block.WHITE_CONCRETE)
            .put(Variant.CONCRETE_POWDER, Block.WHITE_CONCRETE_POWDER)
            .put(Variant.STAINED_GLASS, Block.WHITE_STAINED_GLASS)
            .put(Variant.STAINED_GLASS_PANE, Block.WHITE_STAINED_GLASS_PANE)
            .put(Variant.BANNER, Block.WHITE_BANNER)
            .put(Variant.BED, Block.WHITE_BED)
            .put(Variant.CANDLE, Block.WHITE_CANDLE)
            .put(Variant.CANDLE_CAKE, Block.WHITE_CANDLE_CAKE)
            .put(Variant.CARPET, Block.WHITE_CARPET)
            .put(Variant.DYE, Material.WHITE_DYE)
            .put(Variant.SHULKER_BOX, Material.WHITE_SHULKER_BOX);

    public static final ColorFamily LIGHT_GRAY = new ColorFamily(Block.LIGHT_GRAY_WOOL)
            .put(Variant.TERRACOTTA, Block.LIGHT_GRAY_TERRACOTTA)
            .put(Variant.GLAZED_TERRACOTTA, Block.LIGHT_GRAY_GLAZED_TERRACOTTA)
            .put(Variant.CONCRETE, Block.LIGHT_GRAY_CONCRETE)
            .put(Variant.CONCRETE_POWDER, Block.LIGHT_GRAY_CONCRETE_POWDER)
            .put(Variant.STAINED_GLASS, Block.LIGHT_GRAY_STAINED_GLASS)
            .put(Variant.STAINED_GLASS_PANE, Block.LIGHT_GRAY_STAINED_GLASS_PANE)
            .put(Variant.BANNER, Block.LIGHT_GRAY_BANNER)
            .put(Variant.BED, Block.LIGHT_GRAY_BED)
            .put(Variant.CANDLE, Block.LIGHT_GRAY_CANDLE)
            .put(Variant.CANDLE_CAKE, Block.LIGHT_GRAY_CANDLE_CAKE)
            .put(Variant.CARPET, Block.LIGHT_GRAY_CARPET)
            .put(Variant.DYE, Material.LIGHT_GRAY_DYE)
            .put(Variant.SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX);

    public static final ColorFamily GRAY = new ColorFamily(Block.GRAY_WOOL)
            .put(Variant.TERRACOTTA, Block.GRAY_TERRACOTTA)
            .put(Variant.GLAZED_TERRACOTTA, Block.GRAY_GLAZED_TERRACOTTA)
            .put(Variant.CONCRETE, Block.GRAY_CONCRETE)
            .put(Variant.CONCRETE_POWDER, Block.GRAY_CONCRETE_POWDER)
            .put(Variant.STAINED_GLASS, Block.GRAY_STAINED_GLASS)
            .put(Variant.STAINED_GLASS_PANE, Block.GRAY_STAINED_GLASS_PANE)
            .put(Variant.BANNER, Block.GRAY_BANNER)
            .put(Variant.BED, Block.GRAY_BED)
            .put(Variant.CANDLE, Block.GRAY_CANDLE)
            .put(Variant.CANDLE_CAKE, Block.GRAY_CANDLE_CAKE)
            .put(Variant.CARPET, Block.GRAY_CARPET)
            .put(Variant.DYE, Material.GRAY_DYE)
            .put(Variant.SHULKER_BOX, Material.GRAY_SHULKER_BOX);

    public static final ColorFamily BLACK = new ColorFamily(Block.BLACK_WOOL)
            .put(Variant.TERRACOTTA, Block.BLACK_TERRACOTTA)
            .put(Variant.GLAZED_TERRACOTTA, Block.BLACK_GLAZED_TERRACOTTA)
            .put(Variant.CONCRETE, Block.BLACK_CONCRETE)
            .put(Variant.CONCRETE_POWDER, Block.BLACK_CONCRETE_POWDER)
            .put(Variant.STAINED_GLASS, Block.BLACK_STAINED_GLASS)
            .put(Variant.STAINED_GLASS_PANE, Block.BLACK_STAINED_GLASS_PANE)
            .put(Variant.BANNER, Block.BLACK_BANNER)
            .put(Variant.BED, Block.BLACK_BED)
            .put(Variant.CANDLE, Block.BLACK_CANDLE)
            .put(Variant.CANDLE_CAKE, Block.BLACK_CANDLE_CAKE)
            .put(Variant.CARPET, Block.BLACK_CARPET)
            .put(Variant.DYE, Material.BLACK_DYE)
            .put(Variant.SHULKER_BOX, Material.BLACK_SHULKER_BOX);

    public static final ColorFamily BROWN = new ColorFamily(Block.BROWN_WOOL)
            .put(Variant.TERRACOTTA, Block.BROWN_TERRACOTTA)
            .put(Variant.GLAZED_TERRACOTTA, Block.BROWN_GLAZED_TERRACOTTA)
            .put(Variant.CONCRETE, Block.BROWN_CONCRETE)
            .put(Variant.CONCRETE_POWDER, Block.BROWN_CONCRETE_POWDER)
            .put(Variant.STAINED_GLASS, Block.BROWN_STAINED_GLASS)
            .put(Variant.STAINED_GLASS_PANE, Block.BROWN_STAINED_GLASS_PANE)
            .put(Variant.BANNER, Block.BROWN_BANNER)
            .put(Variant.BED, Block.BROWN_BED)
            .put(Variant.CANDLE, Block.BROWN_CANDLE)
            .put(Variant.CANDLE_CAKE, Block.BROWN_CANDLE_CAKE)
            .put(Variant.CARPET, Block.BROWN_CARPET)
            .put(Variant.DYE, Material.BROWN_DYE)
            .put(Variant.SHULKER_BOX, Material.BROWN_SHULKER_BOX);

    public static final ColorFamily RED = new ColorFamily(Block.RED_WOOL)
            .put(Variant.TERRACOTTA, Block.RED_TERRACOTTA)
            .put(Variant.GLAZED_TERRACOTTA, Block.RED_GLAZED_TERRACOTTA)
            .put(Variant.CONCRETE, Block.RED_CONCRETE)
            .put(Variant.CONCRETE_POWDER, Block.RED_CONCRETE_POWDER)
            .put(Variant.STAINED_GLASS, Block.RED_STAINED_GLASS)
            .put(Variant.STAINED_GLASS_PANE, Block.RED_STAINED_GLASS_PANE)
            .put(Variant.BANNER, Block.RED_BANNER)
            .put(Variant.BED, Block.RED_BED)
            .put(Variant.CANDLE, Block.RED_CANDLE)
            .put(Variant.CANDLE_CAKE, Block.RED_CANDLE_CAKE)
            .put(Variant.CARPET, Block.RED_CARPET)
            .put(Variant.DYE, Material.RED_DYE)
            .put(Variant.SHULKER_BOX, Material.RED_SHULKER_BOX);

    public static final ColorFamily ORANGE = new ColorFamily(Block.ORANGE_WOOL)
            .put(Variant.TERRACOTTA, Block.ORANGE_TERRACOTTA)
            .put(Variant.GLAZED_TERRACOTTA, Block.ORANGE_GLAZED_TERRACOTTA)
            .put(Variant.CONCRETE, Block.ORANGE_CONCRETE)
            .put(Variant.CONCRETE_POWDER, Block.ORANGE_CONCRETE_POWDER)
            .put(Variant.STAINED_GLASS, Block.ORANGE_STAINED_GLASS)
            .put(Variant.STAINED_GLASS_PANE, Block.ORANGE_STAINED_GLASS_PANE)
            .put(Variant.BANNER, Block.ORANGE_BANNER)
            .put(Variant.BED, Block.ORANGE_BED)
            .put(Variant.CANDLE, Block.ORANGE_CANDLE)
            .put(Variant.CANDLE_CAKE, Block.ORANGE_CANDLE_CAKE)
            .put(Variant.CARPET, Block.ORANGE_CARPET)
            .put(Variant.DYE, Material.ORANGE_DYE)
            .put(Variant.SHULKER_BOX, Material.ORANGE_SHULKER_BOX);

    public static final ColorFamily YELLOW = new ColorFamily(Block.YELLOW_WOOL)
            .put(Variant.TERRACOTTA, Block.YELLOW_TERRACOTTA)
            .put(Variant.GLAZED_TERRACOTTA, Block.YELLOW_GLAZED_TERRACOTTA)
            .put(Variant.CONCRETE, Block.YELLOW_CONCRETE)
            .put(Variant.CONCRETE_POWDER, Block.YELLOW_CONCRETE_POWDER)
            .put(Variant.STAINED_GLASS, Block.YELLOW_STAINED_GLASS)
            .put(Variant.STAINED_GLASS_PANE, Block.YELLOW_STAINED_GLASS_PANE)
            .put(Variant.BANNER, Block.YELLOW_BANNER)
            .put(Variant.BED, Block.YELLOW_BED)
            .put(Variant.CANDLE, Block.YELLOW_CANDLE)
            .put(Variant.CANDLE_CAKE, Block.YELLOW_CANDLE_CAKE)
            .put(Variant.CARPET, Block.YELLOW_CARPET)
            .put(Variant.DYE, Material.YELLOW_DYE)
            .put(Variant.SHULKER_BOX, Material.YELLOW_SHULKER_BOX);

    public static final ColorFamily LIME = new ColorFamily(Block.LIME_WOOL)
            .put(Variant.TERRACOTTA, Block.LIME_TERRACOTTA)
            .put(Variant.GLAZED_TERRACOTTA, Block.LIME_GLAZED_TERRACOTTA)
            .put(Variant.CONCRETE, Block.LIME_CONCRETE)
            .put(Variant.CONCRETE_POWDER, Block.LIME_CONCRETE_POWDER)
            .put(Variant.STAINED_GLASS, Block.LIME_STAINED_GLASS)
            .put(Variant.STAINED_GLASS_PANE, Block.LIME_STAINED_GLASS_PANE)
            .put(Variant.BANNER, Block.LIME_BANNER)
            .put(Variant.BED, Block.LIME_BED)
            .put(Variant.CANDLE, Block.LIME_CANDLE)
            .put(Variant.CANDLE_CAKE, Block.LIME_CANDLE_CAKE)
            .put(Variant.CARPET, Block.LIME_CARPET)
            .put(Variant.DYE, Material.LIME_DYE)
            .put(Variant.SHULKER_BOX, Material.LIME_SHULKER_BOX);

    public static final ColorFamily GREEN = new ColorFamily(Block.GREEN_WOOL)
            .put(Variant.TERRACOTTA, Block.GREEN_TERRACOTTA)
            .put(Variant.GLAZED_TERRACOTTA, Block.GREEN_GLAZED_TERRACOTTA)
            .put(Variant.CONCRETE, Block.GREEN_CONCRETE)
            .put(Variant.CONCRETE_POWDER, Block.GREEN_CONCRETE_POWDER)
            .put(Variant.STAINED_GLASS, Block.GREEN_STAINED_GLASS)
            .put(Variant.STAINED_GLASS_PANE, Block.GREEN_STAINED_GLASS_PANE)
            .put(Variant.BANNER, Block.GREEN_BANNER)
            .put(Variant.BED, Block.GREEN_BED)
            .put(Variant.CANDLE, Block.GREEN_CANDLE)
            .put(Variant.CANDLE_CAKE, Block.GREEN_CANDLE_CAKE)
            .put(Variant.CARPET, Block.GREEN_CARPET)
            .put(Variant.DYE, Material.GREEN_DYE)
            .put(Variant.SHULKER_BOX, Material.GREEN_SHULKER_BOX);

    public static final ColorFamily CYAN = new ColorFamily(Block.CYAN_WOOL)
            .put(Variant.TERRACOTTA, Block.CYAN_TERRACOTTA)
            .put(Variant.GLAZED_TERRACOTTA, Block.CYAN_GLAZED_TERRACOTTA)
            .put(Variant.CONCRETE, Block.CYAN_CONCRETE)
            .put(Variant.CONCRETE_POWDER, Block.CYAN_CONCRETE_POWDER)
            .put(Variant.STAINED_GLASS, Block.CYAN_STAINED_GLASS)
            .put(Variant.STAINED_GLASS_PANE, Block.CYAN_STAINED_GLASS_PANE)
            .put(Variant.BANNER, Block.CYAN_BANNER)
            .put(Variant.BED, Block.CYAN_BED)
            .put(Variant.CANDLE, Block.CYAN_CANDLE)
            .put(Variant.CANDLE_CAKE, Block.CYAN_CANDLE_CAKE)
            .put(Variant.CARPET, Block.CYAN_CARPET)
            .put(Variant.DYE, Material.CYAN_DYE)
            .put(Variant.SHULKER_BOX, Material.CYAN_SHULKER_BOX);

    public static final ColorFamily LIGHT_BLUE = new ColorFamily(Block.LIGHT_BLUE_WOOL)
            .put(Variant.TERRACOTTA, Block.LIGHT_BLUE_TERRACOTTA)
            .put(Variant.GLAZED_TERRACOTTA, Block.LIGHT_BLUE_GLAZED_TERRACOTTA)
            .put(Variant.CONCRETE, Block.LIGHT_BLUE_CONCRETE)
            .put(Variant.CONCRETE_POWDER, Block.LIGHT_BLUE_CONCRETE_POWDER)
            .put(Variant.STAINED_GLASS, Block.LIGHT_BLUE_STAINED_GLASS)
            .put(Variant.STAINED_GLASS_PANE, Block.LIGHT_BLUE_STAINED_GLASS_PANE)
            .put(Variant.BANNER, Block.LIGHT_BLUE_BANNER)
            .put(Variant.BED, Block.LIGHT_BLUE_BED)
            .put(Variant.CANDLE, Block.LIGHT_BLUE_CANDLE)
            .put(Variant.CANDLE_CAKE, Block.LIGHT_BLUE_CANDLE_CAKE)
            .put(Variant.CARPET, Block.LIGHT_BLUE_CARPET)
            .put(Variant.DYE, Material.LIGHT_BLUE_DYE)
            .put(Variant.SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX);

    public static final ColorFamily BLUE = new ColorFamily(Block.BLUE_WOOL)
            .put(Variant.TERRACOTTA, Block.BLUE_TERRACOTTA)
            .put(Variant.GLAZED_TERRACOTTA, Block.BLUE_GLAZED_TERRACOTTA)
            .put(Variant.CONCRETE, Block.BLUE_CONCRETE)
            .put(Variant.CONCRETE_POWDER, Block.BLUE_CONCRETE_POWDER)
            .put(Variant.STAINED_GLASS, Block.BLUE_STAINED_GLASS)
            .put(Variant.STAINED_GLASS_PANE, Block.BLUE_STAINED_GLASS_PANE)
            .put(Variant.BANNER, Block.BLUE_BANNER)
            .put(Variant.BED, Block.BLUE_BED)
            .put(Variant.CANDLE, Block.BLUE_CANDLE)
            .put(Variant.CANDLE_CAKE, Block.BLUE_CANDLE_CAKE)
            .put(Variant.CARPET, Block.BLUE_CARPET)
            .put(Variant.DYE, Material.BLUE_DYE)
            .put(Variant.SHULKER_BOX, Material.BLUE_SHULKER_BOX);

    public static final ColorFamily PURPLE = new ColorFamily(Block.PURPLE_WOOL)
            .put(Variant.TERRACOTTA, Block.PURPLE_TERRACOTTA)
            .put(Variant.GLAZED_TERRACOTTA, Block.PURPLE_GLAZED_TERRACOTTA)
            .put(Variant.CONCRETE, Block.PURPLE_CONCRETE)
            .put(Variant.CONCRETE_POWDER, Block.PURPLE_CONCRETE_POWDER)
            .put(Variant.STAINED_GLASS, Block.PURPLE_STAINED_GLASS)
            .put(Variant.STAINED_GLASS_PANE, Block.PURPLE_STAINED_GLASS_PANE)
            .put(Variant.BANNER, Block.PURPLE_BANNER)
            .put(Variant.BED, Block.PURPLE_BED)
            .put(Variant.CANDLE, Block.PURPLE_CANDLE)
            .put(Variant.CANDLE_CAKE, Block.PURPLE_CANDLE_CAKE)
            .put(Variant.CARPET, Block.PURPLE_CARPET)
            .put(Variant.DYE, Material.PURPLE_DYE)
            .put(Variant.SHULKER_BOX, Material.PURPLE_SHULKER_BOX);

    public static final ColorFamily MAGENTA = new ColorFamily(Block.MAGENTA_WOOL)
            .put(Variant.TERRACOTTA, Block.MAGENTA_TERRACOTTA)
            .put(Variant.GLAZED_TERRACOTTA, Block.MAGENTA_GLAZED_TERRACOTTA)
            .put(Variant.CONCRETE, Block.MAGENTA_CONCRETE)
            .put(Variant.CONCRETE_POWDER, Block.MAGENTA_CONCRETE_POWDER)
            .put(Variant.STAINED_GLASS, Block.MAGENTA_STAINED_GLASS)
            .put(Variant.STAINED_GLASS_PANE, Block.MAGENTA_STAINED_GLASS_PANE)
            .put(Variant.BANNER, Block.MAGENTA_BANNER)
            .put(Variant.BED, Block.MAGENTA_BED)
            .put(Variant.CANDLE, Block.MAGENTA_CANDLE)
            .put(Variant.CANDLE_CAKE, Block.MAGENTA_CANDLE_CAKE)
            .put(Variant.CARPET, Block.MAGENTA_CARPET)
            .put(Variant.DYE, Material.MAGENTA_DYE)
            .put(Variant.SHULKER_BOX, Material.MAGENTA_SHULKER_BOX);

    public static final ColorFamily PINK = new ColorFamily(Block.PINK_WOOL)
            .put(Variant.TERRACOTTA, Block.PINK_TERRACOTTA)
            .put(Variant.GLAZED_TERRACOTTA, Block.PINK_GLAZED_TERRACOTTA)
            .put(Variant.CONCRETE, Block.PINK_CONCRETE)
            .put(Variant.CONCRETE_POWDER, Block.PINK_CONCRETE_POWDER)
            .put(Variant.STAINED_GLASS, Block.PINK_STAINED_GLASS)
            .put(Variant.STAINED_GLASS_PANE, Block.PINK_STAINED_GLASS_PANE)
            .put(Variant.BANNER, Block.PINK_BANNER)
            .put(Variant.BED, Block.PINK_BED)
            .put(Variant.CANDLE, Block.PINK_CANDLE)
            .put(Variant.CANDLE_CAKE, Block.PINK_CANDLE_CAKE)
            .put(Variant.CARPET, Block.PINK_CARPET)
            .put(Variant.DYE, Material.PINK_DYE)
            .put(Variant.SHULKER_BOX, Material.PINK_SHULKER_BOX);
}
