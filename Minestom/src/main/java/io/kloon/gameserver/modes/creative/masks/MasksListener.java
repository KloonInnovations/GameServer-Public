package io.kloon.gameserver.modes.creative.masks;

import io.kloon.gameserver.minestom.armor.ArmorFamily;
import io.kloon.gameserver.minestom.events.EventHandler;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.event.item.EntityEquipEvent;
import net.minestom.server.item.ItemStack;

public class MasksListener {
    @EventHandler
    public void onEquip(EntityEquipEvent event) {
        if (!(event.getEntity() instanceof CreativePlayer player)) {
            return;
        }
        if (player.getTicksOnInstance() < 20) {
            return;
        }

        ItemStack item = event.getEquippedItem();
        MaskItem maskItem = MaskItem.get(item);
        if (maskItem == null) {
            return;
        }

        EquipmentSlot slot = event.getSlot();
        if (!slot.isArmor()) {
            return;
        }

        ArmorFamily armorFamily = ArmorFamily.get(item);
        if (armorFamily != null) {
            player.playSound(armorFamily.equipSound(), 0.85 + maskItem.getMasks().size() * 0.1);
        }
    }
}
