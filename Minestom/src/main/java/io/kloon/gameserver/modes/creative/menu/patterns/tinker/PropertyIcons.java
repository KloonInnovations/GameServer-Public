package io.kloon.gameserver.modes.creative.menu.patterns.tinker;

import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.itembuilder.ItemBuilder2;
import io.kloon.gameserver.minestom.itembuilder.SkinCache;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class PropertyIcons {
    public static final Map<String, Supplier<ItemBuilder2>> ICONS = new HashMap<>();
    static {
        ICONS.put("waterlogged", () -> MenuStack.of(Material.WATER_BUCKET));
        ICONS.put("facing", () -> MenuStack.of(Material.COMPASS));
        ICONS.put("powered", () -> MenuStack.of(Material.REDSTONE_TORCH));
        ICONS.put("half", () -> MenuStack.of(Material.BRICK_SLAB));
        ICONS.put("west", () -> MenuStack.ofHead(SkinCache.toHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODFjOTZhNWMzZDEzYzMxOTkxODNlMWJjN2YwODZmNTRjYTJhNjUyNzEyNjMwM2FjOGUyNWQ2M2UxNmI2NGNjZiJ9fX0=")));
        ICONS.put("south", () -> MenuStack.ofHead(SkinCache.toHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjFlMWU3MzBjNzcyNzljOGUyZTE1ZDhiMjcxYTExN2U1ZTJjYTkzZDI1YzhiZTNhMDBjYzkyYTAwY2MwYmI4NSJ9fX0=")));
        ICONS.put("north", () -> MenuStack.ofHead(SkinCache.toHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWNkYjhmNDM2NTZjMDZjNGU4NjgzZTJlNjM0MWI0NDc5ZjE1N2Y0ODA4MmZlYTRhZmYwOWIzN2NhM2M2OTk1YiJ9fX0=")));
        ICONS.put("east", () -> MenuStack.ofHead(SkinCache.toHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzMzYWU4ZGU3ZWQwNzllMzhkMmM4MmRkNDJiNzRjZmNiZDk0YjM0ODAzNDhkYmI1ZWNkOTNkYThiODEwMTVlMyJ9fX0=")));
        ICONS.put("up", () -> MenuStack.ofHead(SkinCache.toHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWVmNjY0ZDUwNmU5NzUzNDkzOTE5ODVjMWNkMDcxY2VhN2Q0NjMxNzYzZTVhMmY5MTRmYTQ3MGNjMmJkYTIwYSJ9fX0=")));
        ICONS.put("type", () -> MenuStack.of(Material.ARMOR_STAND));
        ICONS.put("shape", () -> MenuStack.of(Material.STONE_STAIRS));
        ICONS.put("axis", () -> MenuStack.of(Material.END_ROD));
        ICONS.put("list", () -> MenuStack.of(Material.CAMPFIRE));
        ICONS.put("open", () -> MenuStack.of(Material.OAK_DOOR));
        ICONS.put("rotation", () -> MenuStack.of(Material.SCULK_SENSOR));
        ICONS.put("age", () -> MenuStack.of(Material.EGG));
        ICONS.put("attached", () -> MenuStack.of(Material.OAK_FENCE));
    }

    public static Supplier<ItemBuilder2> get(Block block, String property) {
        Supplier<ItemBuilder2> icon = ICONS.get(property);
        if (icon != null) {
            return icon;
        }
        return () -> MenuStack.of(Material.STICK);
    }
}
