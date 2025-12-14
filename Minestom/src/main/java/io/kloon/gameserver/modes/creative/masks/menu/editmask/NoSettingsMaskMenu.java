package io.kloon.gameserver.modes.creative.masks.menu.editmask;

import io.kloon.gameserver.chestmenus.builtin.StaticButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.toggle.NegateMaskToggleButton;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class NoSettingsMaskMenu extends EditMaskMenu {
    public NoSettingsMaskMenu(EditMaskItemMenu parent, MaskWithData mask) {
        super(parent, mask);
    }

    @Override
    protected void registerButtons() {
        super.registerButtons();

        StaticButton noSettingsButton = mask.type().getIcon()
                .name(MM."<title>No Settings!")
                .lore(new Lore().wrap(MM."<gray>The \{mask.type().getNameMM()}<gray> mask doesn't have any settings to edit!"))
                .buildButton();
        reg(12, noSettingsButton);

        reg(14, new NegateMaskToggleButton(parent, mask));
    }
}
