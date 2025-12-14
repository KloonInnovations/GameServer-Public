package io.kloon.gameserver.minestom.utils;

import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponents;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.apache.commons.text.WordUtils;

import java.util.concurrent.ConcurrentHashMap;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public final class BlockFmt {
    private BlockFmt() {}

    private static final ConcurrentHashMap<Block, String> BLOCK_NAMES = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Material, String> MATERIAL_NAMES = new ConcurrentHashMap<>();

    public static String getName(Block block) {
        return BLOCK_NAMES.computeIfAbsent(block, b -> getNamespace(b.name()));
    }

    public static String getName(Material material) {
        return MATERIAL_NAMES.computeIfAbsent(material, m -> getNamespace(m.name()));
    }

    public static Component getName(ItemStack item) {
        Component customName = item.get(DataComponents.CUSTOM_NAME);
        if (customName != null) {
            return customName;
        }
        return MM."\{getName(item.material())}";
    }

    private static String getNamespace(String name) {
        String[] split = name.split(":");
        String lastSplit = split[split.length - 1];

        String fmtName = lastSplit.replace("_", " ");
        fmtName = WordUtils.capitalize(fmtName);

        return fmtName;
    }
}
