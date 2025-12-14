package io.kloon.gameserver.modes.creative.menu.random;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.ClearCommand;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class ClearInventoryButton implements ChestButton {
    public static final String ICON = "\uD83C\uDF24"; // 🌤

    @Override
    public void clickButton(Player player, ButtonClick click) {
        ClearCommand.clearPlayerInventoryExceptMenu((CreativePlayer) player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<red>\{ICON} <title>Clear Inventory";

        List<Component> lore = new ArrayList<>();
        lore.add(MM."<cmd>\{ClearCommand.LABEL}");
        lore.add(Component.empty());
        lore.addAll(MM_WRAP."<gray>Get rid of all that gabagool and right quick.");
        lore.add(Component.empty());
        lore.add(MM."<cta>Click to clear!");

        return MenuStack.of(CreativeToolType.CLEAR.getMaterial())
                .name(name)
                .lore(lore)
                .build();
    }
}
