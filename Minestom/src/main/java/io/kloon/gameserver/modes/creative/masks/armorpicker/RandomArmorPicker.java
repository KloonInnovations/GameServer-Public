package io.kloon.gameserver.modes.creative.masks.armorpicker;

import io.kloon.gameserver.minestom.armor.ArmorSlot;
import io.kloon.gameserver.util.RandUtil;
import net.minestom.server.item.Material;

import java.util.Arrays;
import java.util.List;

public class RandomArmorPicker implements MaskArmorPicker {
    public static final List<ArmorSlot> POSSIBLE_SLOTS = Arrays.asList(
            ArmorSlot.CHESTPLATE,
            ArmorSlot.LEGGINGS,
            ArmorSlot.BOOTS
    );

    public static final List<Material> POSSIBLE = Arrays.asList(
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS
    );

    @Override
    public Material pick() {
        return RandUtil.getRandom(POSSIBLE);
    }
}
