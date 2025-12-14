package io.kloon.gameserver.modes.creative.tools.menus.toggles;

import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.builtin.ToggleButton;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ToolPreferenceToggleButton<T> extends ToggleButton {
    private final int slot;
    private final CreativeTool<?, T> tool;
    private final ToolToggle<T> toggle;

    private static final ToolDataType dataType = ToolDataType.PLAYER_BOUND;

    public ToolPreferenceToggleButton(int slot, CreativeTool<?, T> tool, ToolToggle<T> toggle) {
        this.slot = slot;
        this.tool = tool;
        this.toggle = toggle;
    }

    @Override
    public boolean isEnabled(Player player) {
        CreativePlayer cp = (CreativePlayer) player;
        T playerBound = tool.getPlayerBound(cp);
        return toggle.isEnabled().apply(playerBound);
    }

    @Override
    public void setEnabled(Player p, boolean enabled) {
        CreativePlayer player = (CreativePlayer) p;

        T playerBound = tool.getPlayerBound(player);
        toggle.editEnabled().apply(playerBound, enabled);
        player.getToolsStorage().set(tool.getType(), playerBound);
    }

    @Override
    public void onValueChange(Player p, boolean newValue) {
        CreativePlayer player = (CreativePlayer) p;
        Component msg = newValue
                ? MM."<gray>Toggled \{getName()} to <green>ON<gray>!"
                : MM."<gray>Toggled \{getName()} to <red>OFF<gray>!";

        double pitch = newValue ? 1.9 : 1.5;
        dataType.sendMsg(player, msg,
                SoundEvent.BLOCK_NOTE_BLOCK_PLING, pitch);

        ChestMenuInv.rerenderButton(slot, p);
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
    public final List<Component> getDescription() {
        List<Component> lore = dataType.lore();
        lore.addAll(toggle.lore());
        return lore;
    }
}
