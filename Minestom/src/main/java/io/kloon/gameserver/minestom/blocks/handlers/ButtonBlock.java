package io.kloon.gameserver.minestom.blocks.handlers;

import com.google.common.collect.Sets;
import io.kloon.gameserver.minestom.blocks.properties.BooleanProp;
import net.kyori.adventure.key.Key;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public class ButtonBlock extends AttachedFacingXZBlock implements BlockHandler {
    public static final BooleanProp POWERED = new BooleanProp("powered");

    public static final Set<Material> MATERIALS = Sets.newHashSet(
            Material.OAK_BUTTON,
            Material.SPRUCE_BUTTON,
            Material.BIRCH_BUTTON,
            Material.JUNGLE_BUTTON,
            Material.ACACIA_BUTTON,
            Material.CHERRY_BUTTON,
            Material.DARK_OAK_BUTTON,
            Material.MANGROVE_BUTTON,
            Material.BAMBOO_BUTTON,
            Material.CRIMSON_BUTTON,
            Material.WARPED_BUTTON,
            Material.STONE_BUTTON,
            Material.POLISHED_BLACKSTONE_BUTTON
    );

    public static final Set<Block> BLOCKS = MATERIALS.stream().map(Material::block).collect(Collectors.toSet());

    @Override
    public @NotNull Key getKey() {
        return Key.key("kloon:button");
    }
}
