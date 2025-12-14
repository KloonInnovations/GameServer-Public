package io.kloon.gameserver.modes.creative.tools.impl.tinker;

import com.google.common.base.Strings;
import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.blocks.KloonPlacementRules;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.minestom.itembuilder.SkinCache;
import io.kloon.gameserver.minestom.nbt.NBT;
import io.kloon.gameserver.minestom.utils.BlockFmt;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public record TinkeredBlock(Block block, boolean showTinkeredOnDefaultState) {
    public TinkeredBlock(Block block) {
        this(block, false);
    }

    private static final String RAINBOW = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY3YzU0ZmY3ODYyMTE2ZTY1YTE0MzY2MjBiOTFhZjU4YjUyYWIxNzE1MmExODM3MTgwZjM0NTgwMzJmNTcwMiJ9fX0=";
    public static final TextColor COLOR = TextColor.color(108, 247, 61);

    public static final Tag<BinaryTag> TAG = Tag.NBT("tinker");

    public ItemStack toItem() {
        Material material = block.registry().material();
        ItemBuilder2 builder;
        if (material == null) {
            builder = MenuStack.ofHead(SkinCache.toHead(RAINBOW));
        } else {
            if (block == block.defaultState()) {
                return ItemStack.of(material);
            }

            builder = MenuStack.of(material);
        }

        Map<String, Collection<String>> options = block.propertyOptions();
        if (options.isEmpty() && material != null) {
            return builder.build();
        }
        List<String> optionKeys = options.keySet().stream().sorted().toList();

        Component name = MM."\{getNameMM(block)}";

        Lore lore = new Lore();
//        lore.add("<dark_gray>Tinkered");
//        lore.addEmpty();

        if (options.isEmpty()) {
            lore.add(MM."<title>Block: <green>\{BlockFmt.getName(block)}");
            lore.wrap("<dark_gray>No associated item for block!");
        } else {
            options.forEach((key, _) -> {
                String value = block.getProperty(key);
                lore.add(MM."<title>\{key}: <white>\{value}");
            });
        }

        if (block.hasNbt()) {
            lore.add(MM."<dark_gray>Has block entity!");
        }

        CompoundBinaryTag tinkerTag = NBT.compound(c -> {
            c.putString("block", block.name());
            c.putBoolean("tinker_on_default", showTinkeredOnDefaultState);

            c.put("properties", NBT.compound(props -> {
                optionKeys.forEach(key -> {
                    String value = block.getProperty(key);
                    if (value == null) return;
                    props.putString(key, value);
                });
            }));

            if (block.nbt() != null) {
                c.put("block_nbt", block.nbt());
            }
            if (block.handler() != null) {
                c.putString("block_handler", block.handler().getKey().asString());
            }
        });
        builder.tag(TAG, tinkerTag);

        return builder.name(name).lore(lore).build();
    }

    public Lore propertiesLore() {
        Lore lore = new Lore();

        List<String> properties = block.propertyOptions().keySet().stream().sorted().toList();
        properties.forEach(property -> {
            String value = block.getProperty(property);
            lore.add(MM."<title>\{property}: <white>\{value}");
        });

        return lore;
    }

    @Nullable
    public static TinkeredBlock get(ItemStack item) {
        BinaryTag tinkerNbt = item.getTag(TAG);
        if (!(tinkerNbt instanceof CompoundBinaryTag compound)) {
            return null;
        }

        String blockName = compound.getString("block");
        Block block = Block.fromKey(blockName);
        if (block == null) {
            return null;
        }

        boolean tinkerOnDefault = compound.getBoolean("tinker_on_default", false);

        Map<String, Collection<String>> options = block.propertyOptions();

        Map<String, String> properties = new HashMap<>();
        CompoundBinaryTag propertiesNbt = compound.getCompound("properties");
        propertiesNbt.forEach(entry -> {
            if (!(entry.getValue() instanceof StringBinaryTag strTag)) return;
            if (!options.containsKey(entry.getKey())) return;
            properties.put(entry.getKey(), strTag.value());
        });
        block = block.withProperties(properties);

        BinaryTag blockNbt = compound.get("block_nbt");
        if (blockNbt instanceof CompoundBinaryTag blockCompound) {
            block = block.withNbt(blockCompound);
        }

        String handlerId = compound.getString("block_handler");
        if (!Strings.isNullOrEmpty(handlerId)) {
            BlockHandler handler = MinecraftServer.getBlockManager().getHandlerOrDummy(handlerId);
            block = block.withHandler(handler);
        }

        return new TinkeredBlock(block, tinkerOnDefault);
    }

    public String getNameMM() {
        if (is(block) || showTinkeredOnDefaultState) {
            return STR."<green>\{BlockFmt.getName(block)}<#FF266E>*";
        }
        return STR."<green>\{BlockFmt.getName(block)}";
    }

    public static boolean is(ItemStack item) {
        return item != null && item.hasTag(TAG);
    }

    public static boolean is(Block block) {
        return block != block.defaultState();
    }

    public static String getNameMM(Block block) {
        if (is(block)) {
            return STR."<\{COLOR.asHexString()}>\{BlockFmt.getName(block)}<#FF266E>*";
        }
        return STR."<\{COLOR.asHexString()}>\{BlockFmt.getName(block)}";
    }
}
