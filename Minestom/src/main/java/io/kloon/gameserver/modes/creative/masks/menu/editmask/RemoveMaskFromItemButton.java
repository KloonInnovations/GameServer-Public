package io.kloon.gameserver.modes.creative.masks.menu.editmask;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.masks.MaskItem;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class RemoveMaskFromItemButton implements ChestButton {
    private final EditMaskItemMenu menu;
    private final MaskWithData<?> mask;

    public RemoveMaskFromItemButton(EditMaskItemMenu menu, MaskWithData<?> mask) {
        this.menu = menu;
        this.mask = mask;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        MaskItem editedItem = menu.getMaskItem().withRemovedMask(mask.id());
        menu.updateMaskAndDisplay(player, editedItem);

        ToolDataType.MASK_BOUND.sendPit(player, MM."<gray>Removed \{mask.type().getNameMM()} <gray>from the item!");
        player.playSound(SoundEvent.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, Pitch.rng(0.6, 0.2), 0.8);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Remove Mask from Item";

        List<MaskWithData<?>> masks = menu.getMaskItem().getMasks();

        Lore lore = new Lore();
        lore.wrap(MM."<gray>There are <\{MaskItem.TEXT_HEX}>\{masks.size()} <gray>masks on the item, maybe that's too much?");
        lore.addEmpty();
        lore.add("<cta>Click to remove mask!");

        return MenuStack.of(Material.TNT, name, lore);
    }
}
