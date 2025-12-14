package io.kloon.gameserver.modes.creative.patterns.menu.toggle;

import io.kloon.gameserver.chestmenus.builtin.ToggleButton;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.menu.EditPatternMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolToggle;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PatternToggleButton<Pattern extends CreativePattern> extends ToggleButton {
    private final EditPatternMenu<Pattern> menu;
    private final ToolToggle<Pattern> toggle;

    private static final ToolDataType DATA_TYPE = ToolDataType.PATTERN_BOUND;

    public PatternToggleButton(EditPatternMenu<Pattern> menu, ToolToggle<Pattern> toggle) {
        this.menu = menu;
        this.toggle = toggle;
    }

    @Override
    public boolean isEnabled(Player player) {
        Pattern pattern = menu.getPattern();
        return toggle.isEnabled().apply(pattern);
    }

    @Override
    public void setEnabled(Player p, boolean enabled) {
        CreativePlayer player = (CreativePlayer) p;

        Pattern pattern = menu.getPattern();
        pattern = toggle.editEnabled().apply(pattern, enabled);
        menu.updateAndDisplay(player, pattern);
    }

    @Override
    public void onValueChange(Player p, boolean newValue) {
        CreativePlayer player = (CreativePlayer) p;
        Component msg = newValue
                ? MM."<gray>Toggled \{getName()} to <green>ON<gray>!"
                : MM."<gray>Toggled \{getName()} to <red>OFF<gray>!";

        double pitch = newValue ? 1.9 : 1.5;
        DATA_TYPE.sendMsg(player, msg, SoundEvent.BLOCK_NOTE_BLOCK_PLING, pitch);
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
