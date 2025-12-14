package io.kloon.gameserver.modes.creative.masks.menu.add;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.masks.MaskItem;
import io.kloon.gameserver.modes.creative.masks.MaskType;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class AddMaskToItemButton implements ChestButton {
    private final EditMaskItemMenu menu;
    private final MaskType<?> mask;

    public AddMaskToItemButton(EditMaskItemMenu menu, MaskType<?> mask) {
        this.menu = menu;
        this.mask = mask;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        MaskWithData<?> maskWithData = mask.createDefault();
        MaskItem editedMaskItem = menu.getMaskItem().withAddedMask(maskWithData);

        menu.updateMaskAndDisplay(player, editedMaskItem);

        ToolDataType.MASK_BOUND.sendMsg(player, MM."<gray>Added \{mask.getNameMM()} <gray>to the wearable item!",
                SoundEvent.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1.9, 0.8);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>\{mask.getName()}";

        Lore lore = new Lore();

        lore.add(mask.getDatalessDescription());
        lore.addEmpty();

        lore.add("<cta>Click to add to item!");

        return mask.getIcon().name(name).lore(lore).build();
    }
}
