package io.kloon.gameserver.modes.creative.tools.menus.toggles;

import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.builtin.ToggleButton;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class ToolSettingToggleButton<T> extends ToggleButton {
    private final int slot;
    private final CreativeTool<T, ?> tool;
    private final ItemRef itemRef;
    private final ToolToggle<T> toggle;

    private static final ToolDataType DATA_TYPE = ToolDataType.ITEM_BOUND;

    public ToolSettingToggleButton(int slot, CreativeTool<T, ?> tool, ItemRef itemRef, ToolToggle<T> toggle) {
        this.slot = slot;
        this.tool = tool;
        this.itemRef = itemRef;
        this.toggle = toggle;
    }

    @Override
    public boolean isEnabled(Player player) {
        T itemBound = tool.getItemBound(itemRef);
        return toggle.isEnabled().apply(itemBound);
    }

    @Override
    public void setEnabled(Player p, boolean enabled) {
        CreativePlayer player = (CreativePlayer) p;
        tool.editItemBound(player, itemRef, settings -> toggle.editEnabled().apply(settings, enabled));
    }

    @Override
    public void onValueChange(Player p, boolean newValue) {
        CreativePlayer player = (CreativePlayer) p;
        Component msg = newValue
                ? MM."<gray>Toggled \{getName()} to <green>ON<gray>!"
                : MM."<gray>Toggled \{getName()} to <red>OFF<gray>!";

        double pitch = newValue ? 1.9 : 1.5;
        DATA_TYPE.sendMsg(player, msg, SoundEvent.BLOCK_NOTE_BLOCK_PLING, pitch);

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
    public List<Component> getDescription() {
        List<Component> lore = DATA_TYPE.lore();
        lore.addAll(toggle.lore());
        return lore;
    }
}
