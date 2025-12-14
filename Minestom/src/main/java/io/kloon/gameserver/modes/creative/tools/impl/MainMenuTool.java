package io.kloon.gameserver.modes.creative.tools.impl;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.CreativeMainMenu;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.click.impl.InventoryToolClick;
import io.kloon.gameserver.modes.creative.tools.generics.NoDataTool;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class MainMenuTool extends NoDataTool {
    public MainMenuTool() {
        super(CreativeToolType.MENU);
    }

    @Override
    protected boolean canUseWithoutEditPerm(CreativePlayer player, ToolClick click) {
        return true;
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        if (click instanceof InventoryToolClick invClick) {
            invClick.getEvent().setCancelled(true);
        }

        displayMenu(player);
    }

    @Override
    public void handleClickInInventory(CreativePlayer player, InventoryToolClick click) {
        displayMenu(player);
    }

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        displayMenu(player);
    }

    @Override
    public void writeUsage(List<Component> lore, Void settings, Void preferences) {
        lore.add(MM."\{InputFmt.CLICK_GREEN} <#FF266E><b>OPEN");
        lore.addAll(MM_WRAP."<gray>What this does is a closely guarded secret.");
    }

    public void displayMenu(CreativePlayer player) {
        new CreativeMainMenu(player).display(player);
    }
}
