package io.kloon.gameserver.modes.creative.masks.menu.common;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.masks.MaskItem;
import io.kloon.gameserver.modes.creative.masks.MasksUnion;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ToggleUnionButton implements ChestButton {
    private final EditMaskItemMenu menu;

    public ToggleUnionButton(EditMaskItemMenu menu) {
        this.menu = menu;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        MaskItem maskItem = menu.getMaskItem();
        MasksUnion toggled = maskItem.getUnion().toggle();
        MaskItem edited = maskItem.withUnion(toggled);
        menu.updateMaskAndDisplay(player, edited);

        ToolDataType.MASK_BOUND.sendPit(player, MM."<gray>Switched mask union to \{toggled.getNameMM()}<gray>!");
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>Mask Combinations";

        Lore lore = new Lore();
        lore.wrap("<gray>Switch how the masks combine within this item to be <green>OK<gray>.");
        lore.addEmpty();

        MasksUnion union = menu.getMaskItem().getUnion();
        lore.add(MM."<gray>Union: \{union.getNameMM()}");
        String rule = switch (union) {
            case OR -> "1+ condition must be true";
            case AND -> "ALL conditions must be true";
        };
        lore.add(MM."<green>OK <gray>= <green>\{rule}");
        lore.addEmpty();

        lore.wrap("<gray>All armor pieces you wear must be <green>OK <gray>for a block to be edited.");
        if (union == MasksUnion.AND) {
            lore.addEmpty();
            if (ThreadLocalRandom.current().nextInt(6) == 0) {
                lore.wrap("<dark_gray>Yes, you can setup impossible masks. You can also look at the sun, but it's not recommended.");
            } else {
                lore.wrap("<dark_gray>Yes, you can setup impossible masks.");
            }
        }
        lore.addEmpty();
        lore.add("<cta>Click to toggle!");

        return MenuStack.of(union.getIcon(), name, lore);
    }
}
