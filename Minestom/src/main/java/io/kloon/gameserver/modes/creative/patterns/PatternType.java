package io.kloon.gameserver.modes.creative.patterns;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.io.MinecraftCodec;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.modes.creative.menu.patterns.BlockSelectionMenu;
import io.kloon.gameserver.modes.creative.patterns.impl.grid.GridPattern;
import io.kloon.gameserver.modes.creative.patterns.impl.grid.menu.GridPatternMenu;
import io.kloon.gameserver.modes.creative.patterns.impl.weighted.menu.WeightedPatternMenu;
import io.kloon.gameserver.modes.creative.menu.util.CreativeConsumer;
import io.kloon.gameserver.modes.creative.patterns.impl.PassthroughPattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.patterns.impl.weighted.WeightedPattern;
import io.kloon.gameserver.util.weighted.WeightedTable;
import io.kloon.infra.util.EnumQuery;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Arrays;
import java.util.List;

public enum PatternType {
    SINGLE_BLOCK_LEGACY(0, SingleBlockPattern.OLD_CODEC, "Block", Material.STONE, "A single block!"),
    PASSTHROUGH(2, PassthroughPattern.CODEC, "Passthrough", Material.GLASS, "Whatever block is edited remains the same."),
    WEIGHTED_RANDOM(1, WeightedPattern.CODEC, "Weighted Pattern", Material.LIGHT_GRAY_GLAZED_TERRACOTTA, "Pick blocks randomly based on weights that you assign."),
    GRID(3, GridPattern.CODEC, "Grid", Material.SPAWNER, "A tessellation of cuboids."),
    SINGLE_BLOCK(4, SingleBlockPattern.CODEC, "Block", Material.STONE, "A single block!"),
    ;

    private final int dbKey;
    private final MinecraftCodec<? extends CreativePattern> codec;
    private final String name;
    private final Material material;
    private final String description;

    PatternType(int dbKey, MinecraftCodec<? extends CreativePattern> codec, String name, Material material, String description) {
        this.dbKey = dbKey;
        this.codec = codec;
        this.name = name;
        this.material = material;
        this.description = description;
    }

    public int getDbKey() {
        return dbKey;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPropertyName() {
        return this == SINGLE_BLOCK_LEGACY || this == SINGLE_BLOCK
                ? "Block"
                : "Pattern";
    }

    public ItemBuilder2 icon() {
        return MenuStack.of(material);
    }

    public MinecraftCodec<? extends CreativePattern> getCodec() {
        return codec;
    }

    public CreativePattern createDefaultPattern() {
        return switch (this) {
            case SINGLE_BLOCK_LEGACY, SINGLE_BLOCK -> new SingleBlockPattern(Block.STONE);
            case PASSTHROUGH -> new PassthroughPattern();
            case WEIGHTED_RANDOM -> {
                WeightedTable<CreativePattern> table = new WeightedTable<>();
                table.put(new SingleBlockPattern(Block.STONE), 10);
                table.put(new SingleBlockPattern(Block.AIR), 10);
                yield new WeightedPattern(table);
            }
            case GRID -> new GridPattern();
        };
    }

    public boolean hasEditMenu() {
        return switch (this) {
            case SINGLE_BLOCK_LEGACY, SINGLE_BLOCK, WEIGHTED_RANDOM, GRID -> true;
            default -> false;
        };
    }

    @UnknownNullability
    public ChestMenu createEditMenu(ChestMenu parent, CreativePattern existing, CreativeConsumer<CreativePattern> onUpdate) {
        return switch (this) {
            case SINGLE_BLOCK_LEGACY, SINGLE_BLOCK -> {
                CreativeConsumer<Block> consumer = (player, block) -> onUpdate.accept(player, new SingleBlockPattern(block));
                yield new BlockSelectionMenu(parent, consumer);
            }
            case WEIGHTED_RANDOM -> new WeightedPatternMenu(parent, (WeightedPattern) existing, onUpdate);
            case GRID -> new GridPatternMenu(parent, (GridPattern) existing, onUpdate);
            default -> null;
        };
    }

    public static final EnumQuery<Integer, PatternType> BY_DB_KEY = new EnumQuery<>(PatternType.values(), t -> t.dbKey);
    public static final List<PatternType> TYPES_EXCEPT_SINGLE = Arrays.stream(values()).filter(p -> p != SINGLE_BLOCK_LEGACY && p != SINGLE_BLOCK).toList();
}
