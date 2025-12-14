package io.kloon.gameserver.modes.creative.masks.armorpicker;

import io.kloon.gameserver.minestom.armor.ArmorFamily;
import io.kloon.gameserver.minestom.armor.ArmorSlot;
import io.kloon.gameserver.modes.creative.masks.MaskItem;
import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class PlayerArmorPicker implements MaskArmorPicker {
    private final KloonPlayer player;

    public PlayerArmorPicker(KloonPlayer player) {
        this.player = player;
    }

    @Override
    public Material pick() {
        PlayerInventory inv = player.getInventory();
        Map<ArmorSlot, Integer> slotCounts = new HashMap<>();
        RandomArmorPicker.POSSIBLE_SLOTS.forEach(slot -> slotCounts.put(slot, 0));

        for (int i = 0; i < inv.getSize(); ++i) {
            ItemStack item = inv.getItemStack(i);
            if (!MaskItem.is(item)) continue;
            ArmorSlot slot = ArmorSlot.get(item);
            slotCounts.compute(slot, (_, prev) -> prev == null ? 1 : prev + 1);
        }
        if (slotCounts.isEmpty()) {
            return new RandomArmorPicker().pick();
        }
        ArmorSlot leastCommon = slotCounts.entrySet().stream()
                .min(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey).orElse(ArmorSlot.CHESTPLATE);
        return ArmorFamily.LEATHER.get(leastCommon);
    }
}
