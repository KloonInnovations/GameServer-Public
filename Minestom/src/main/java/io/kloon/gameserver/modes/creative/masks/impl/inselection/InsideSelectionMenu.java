package io.kloon.gameserver.modes.creative.masks.impl.inselection;

import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.EditMaskMenu;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.toggle.MaskToggle;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.toggle.MaskToggleButton;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.toggle.NegateMaskToggleButton;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.masks.impl.inselection.InsideSelectionMask.*;

public class InsideSelectionMenu extends EditMaskMenu<Data> {
    public static final MaskToggle<Data> INACTIVE_WHEN_NO_SELECTION = new MaskToggle<>(
            Material.WAXED_EXPOSED_COPPER_DOOR, "Ignore When No Selection",
            MM_WRAP."<gray>When enabled, this mask will have no effect when you have no selection, like you're not wearing it.",
            Data::isInactiveOnNoSelection, Data::setInactiveOnNoSelection);

    public InsideSelectionMenu(EditMaskItemMenu parent, MaskWithData<Data> mask) {
        super(parent, mask);
    }

    @Override
    protected void registerButtons() {
        super.registerButtons();

        reg(12, slot -> new MaskToggleButton<>(slot, this, INACTIVE_WHEN_NO_SELECTION));
        reg(14, new NegateMaskToggleButton(this));
    }
}
