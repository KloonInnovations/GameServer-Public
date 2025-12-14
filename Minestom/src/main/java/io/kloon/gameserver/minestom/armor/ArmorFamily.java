package io.kloon.gameserver.minestom.armor;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.UnknownNullability;

import java.util.HashMap;
import java.util.Map;

import static net.minestom.server.item.Material.*;

public enum ArmorFamily {
    LEATHER(LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS, SoundEvent.ITEM_ARMOR_EQUIP_LEATHER),
    CHAINMAIL(CHAINMAIL_HELMET, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS, SoundEvent.ITEM_ARMOR_EQUIP_CHAIN),
    IRON(IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS, SoundEvent.ITEM_ARMOR_EQUIP_IRON),
    GOLDEN(GOLDEN_HELMET, GOLDEN_CHESTPLATE, GOLDEN_LEGGINGS, GOLDEN_BOOTS, SoundEvent.ITEM_ARMOR_EQUIP_GOLD),
    DIAMOND(DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS, SoundEvent.ITEM_ARMOR_EQUIP_DIAMOND),
    NETHERITE(NETHERITE_HELMET, NETHERITE_CHESTPLATE, NETHERITE_LEGGINGS, NETHERITE_BOOTS, SoundEvent.ITEM_ARMOR_EQUIP_NETHERITE),
    ;

    private final Material helmet;
    private final Material chestplate;
    private final Material leggings;
    private final Material boots;
    private final SoundEvent equipSound;

    ArmorFamily(Material helmet, Material chestplate, Material leggings, Material boots, SoundEvent equipSound) {
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.equipSound = equipSound;
    }

    public Material helmet() {
        return helmet;
    }

    public Material chestplate() {
        return chestplate;
    }

    public Material leggings() {
        return leggings;
    }

    public Material boots() {
        return boots;
    }

    public SoundEvent equipSound() {
        return equipSound;
    }

    public Material get(ArmorSlot slot) {
        return switch (slot) {
            case HELMET -> helmet;
            case CHESTPLATE -> chestplate;
            case LEGGINGS -> leggings;
            case BOOTS -> boots;
        };
    }

    private static final Map<Material, ArmorFamily> FAMILY_BY_MATERIAL = new HashMap<>();
    static {
        for (ArmorFamily family : values()) {
            FAMILY_BY_MATERIAL.put(family.helmet, family);
            FAMILY_BY_MATERIAL.put(family.chestplate, family);
            FAMILY_BY_MATERIAL.put(family.leggings, family);
            FAMILY_BY_MATERIAL.put(family.boots, family);
        }
    }

    @UnknownNullability
    public static ArmorFamily get(Material material) {
        return FAMILY_BY_MATERIAL.get(material);
    }

    @UnknownNullability
    public static ArmorFamily get(ItemStack item) {
        return get(item.material());
    }
}
