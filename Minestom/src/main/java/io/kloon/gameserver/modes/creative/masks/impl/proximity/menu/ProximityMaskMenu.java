package io.kloon.gameserver.modes.creative.masks.impl.proximity.menu;

import io.kloon.gameserver.chestmenus.listing.cycle.Cycle;
import io.kloon.gameserver.chestmenus.listing.cycle.CycleButton;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.impl.proximity.ProximityAxis;
import io.kloon.gameserver.modes.creative.masks.impl.proximity.ProximityMask;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.EditMaskMenu;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.toggle.MaskToggle;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.toggle.MaskToggleButton;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class ProximityMaskMenu extends EditMaskMenu<ProximityMask.Data> {
    private static final MaskToggle<ProximityMask.Data> ONLY_EDIT_AIR = new MaskToggle<>(
            Material.MILK_BUCKET, "Only Edit Air",
            MM_WRAP."<gray>If enabled, only air blocks will be match with this mask.",
            ProximityMask.Data::isOnlyEditAir, ProximityMask.Data::setOnlyEditAir);

    public ProximityMaskMenu(EditMaskItemMenu parent, MaskWithData<ProximityMask.Data> mask) {
        super(parent, mask);
    }

    @Override
    protected void registerButtons() {
        super.registerButtons();

        reg(11, slot -> new ProximityRangeButton(slot, this));

        Cycle<ProximityAxis> axisCycle = new Cycle<>(ProximityAxis.values());
        axisCycle.select(mask.data().getAxis());
        reg(13, slot -> new CycleButton<>(axisCycle, slot)
                .withIcon(() -> axisCycle.getSelected().getIcon())
                .withTitle(MM."<title>Axis")
                .withDescription(MM_WRAP."<gray>Restrain the proximity check to an axis.")
                .withOnClick((p, axis) -> {
                    CreativePlayer player = (CreativePlayer) p;
                    mask.data().setAxis(axis);
                    parent.updateMaskAndDisplay(player, mask);

                    ToolDataType.ITEM_BOUND.sendMsg(player, MM."<gray>Set proximity axis to \{axis.label()}<gray>!",
                            SoundEvent.BLOCK_BAMBOO_WOOD_TRAPDOOR_CLOSE, 0.65 + axis.ordinal() * 0.05);
                }));

        reg(15, slot -> new MaskToggleButton<>(slot, this, ONLY_EDIT_AIR));
    }
}
