package io.kloon.gameserver.modes.creative.menu.masks;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.masks.MaskCommand;
import io.kloon.gameserver.modes.creative.masks.MaskItem;
import io.kloon.gameserver.modes.creative.masks.MaskType;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class MaskPickupButton implements ChestButton {
    private final MaskType<?> mask;

    public MaskPickupButton(MaskType<?> mask) {
        this.mask = mask;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        ItemStack given = mask.giveToPlayer(player);
        int slot = player.getInventoryExtras().getSlot(given);
        if (given == null || slot < 0) {
            return;
        }

        MaskItem maskItem = MaskItem.get(given);
        if (maskItem == null) {
            return;
        }

        ItemRef itemRef = ItemRef.slot(player, slot);
        new EditMaskItemMenu(player, maskItem, itemRef).display(player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>\{mask.getName()}";

        Lore lore = new Lore();
        lore.add(MM."<cmd>\{MaskCommand.ONE_LETTER} \{mask.getCommandLabel()}");
        lore.addEmpty();

        lore.add(mask.getDatalessDescription());
        lore.addEmpty();

        lore.add("<cta>Click to pickup!");

        return mask.getIcon().name(name).lore(lore).build();
    }
}
