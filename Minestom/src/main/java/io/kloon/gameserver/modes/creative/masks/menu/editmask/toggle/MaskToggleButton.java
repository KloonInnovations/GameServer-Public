package io.kloon.gameserver.modes.creative.masks.menu.editmask.toggle;

import io.kloon.gameserver.chestmenus.builtin.ToggleButton;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.EditMaskMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class MaskToggleButton<T> extends ToggleButton {
    private final int slot;
    private final EditMaskMenu<T> menu;
    private final MaskToggle<T> toggle;

    private static final ToolDataType DATA_TYPE = ToolDataType.MASK_BOUND;

    public MaskToggleButton(int slot, EditMaskMenu<T> menu, MaskToggle<T> toggle) {
        this.slot = slot;
        this.menu = menu;
        this.toggle = toggle;
    }

    @Override
    public boolean isEnabled(Player player) {
        MaskWithData<T> mask = menu.getMask();
        return toggle.isEnabled().apply(mask.data());
    }

    @Override
    public void setEnabled(Player p, boolean enabled) {
        CreativePlayer player = (CreativePlayer) p;
        MaskWithData<T> mask = menu.getMask();
        toggle.setEnabled().accept(mask.data(), enabled);
        menu.updateMaskAndDisplay(player, mask);
    }

    @Override
    public void onValueChange(Player p, boolean newValue) {
        CreativePlayer player = (CreativePlayer) p;
        Component msg = newValue
                ? MM."<gray>Toggled \{getName()} to <green>ON<gray>!"
                : MM."<gray>Toggled \{getName()} to <red>OFF<gray>!";

        DATA_TYPE.sendPit(player, msg);

        double pitch = newValue ? 1.9 : 1.5;
        player.playSound(SoundEvent.BLOCK_NOTE_BLOCK_DIDGERIDOO, pitch);
    }

    @Override
    public Material getIconMaterial(boolean enabled) {
        return toggle.icon();
    }

    @Override
    public String getName() {
        return toggle.name();
    }

    @Override
    public List<Component> getDescription() {
        List<Component> lore = DATA_TYPE.lore();
        lore.addAll(toggle.lore());
        return lore;
    }
}
