package io.kloon.gameserver.minestom.armor;

import com.google.common.collect.Sets;
import io.kloon.gameserver.util.WordUtilsK;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public enum ArmorSlot {
    HELMET("\uD83D\uDC52", Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.IRON_HELMET, Material.GOLDEN_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET, Material.TURTLE_HELMET, Material.PLAYER_HEAD),
    CHESTPLATE("\uD83D\uDC55", Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE),
    LEGGINGS("\uD83D\uDC56", Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS),
    BOOTS("\uD83D\uDC5F", Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS),
    ;

    private final String name;
    private final String icon;
    private final Set<Material> materials;

    ArmorSlot(String icon, Material... materials) {
        this.name = WordUtilsK.enumName(this);
        this.icon = icon;
        this.materials = Sets.newHashSet(materials);
    }

    public String getName() {
        return name;
    }

    public String getNameMM() {
        return STR."\{icon} \{name}";
    }

    public String icon() {
        return icon;
    }

    public Set<Material> materials() {
        return materials;
    }

    public void set(Player player, ItemStack item) {
        switch (this) {
            case HELMET -> player.setHelmet(item);
            case CHESTPLATE -> player.setChestplate(item);
            case LEGGINGS -> player.setLeggings(item);
            case BOOTS -> player.setBoots(item);
        }
    }

    public ItemStack get(Player player) {
        return switch (this) {
            case HELMET -> player.getHelmet();
            case CHESTPLATE -> player.getChestplate();
            case LEGGINGS -> player.getLeggings();
            case BOOTS -> player.getBoots();
        };
    }

    public static final ArmorSlot[] VALUES = values();

    private static final Map<Material, ArmorSlot> SLOT_BY_MATERIAL = new HashMap<>();
    static {
        for (ArmorSlot slot : values()) {
            slot.materials().forEach(mat -> SLOT_BY_MATERIAL.put(mat, slot));
        }
    }

    @UnknownNullability
    public static ArmorSlot get(Material material) {
        return SLOT_BY_MATERIAL.get(material);
    }

    @UnknownNullability
    public static ArmorSlot get(ItemStack item) {
        return get(item.material());
    }
}
