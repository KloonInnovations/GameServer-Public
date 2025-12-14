package io.kloon.gameserver.modes.creative.masks.menu.editmask.toggle;

import io.kloon.gameserver.chestmenus.builtin.ToggleButton;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.masks.MaskWithData;
import io.kloon.gameserver.modes.creative.masks.menu.EditMaskItemMenu;
import io.kloon.gameserver.modes.creative.masks.menu.editmask.EditMaskMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class NegateMaskToggleButton extends ToggleButton {
    private final EditMaskItemMenu menu;
    private final MaskWithData<?> mask;

    private static final ToolDataType DATA_TYPE = ToolDataType.MASK_BOUND;

    public NegateMaskToggleButton(EditMaskItemMenu menu, MaskWithData<?> mask) {
        this.menu = menu;
        this.mask = mask;
    }

    public NegateMaskToggleButton(EditMaskMenu<?> menu) {
        this(menu.getParent(), menu.getMask());
    }

    @Override
    public boolean isEnabled(Player player) {
        return mask.negated();
    }

    @Override
    public void setEnabled(Player p, boolean enabled) {
        CreativePlayer player = (CreativePlayer) p;
        MaskWithData<?> edited = mask.withNegated(enabled);
        menu.updateMaskAndDisplay(player, edited);
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
        return enabled ? Material.RED_BANNER : Material.LIME_DYE;
    }

    @Override
    public String getName() {
        return "Negate Mask";
    }

    @Override
    protected Component getStateLine(boolean enabled) {
        return enabled ? MM."<gray>State: <red>Negated!" : MM."<gray>State: <green>Normal";
    }

    @Override
    public List<Component> getDescription() {
        return MM_WRAP."<gray>Make the mask do the inverse of what it usually does.";
    }
}
