package io.kloon.gameserver.modes.creative.menu.preferences.toggles;

import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.builtin.ToggleButton;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.storage.playerdata.CreativePlayerStorage;
import io.kloon.gameserver.modes.creative.tools.menus.ToolDataType;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PlayerStorageToggleButton extends ToggleButton {
    private final int slot;
    private final PlayerStorageToggle toggle;

    private static final ToolDataType dataType = ToolDataType.PLAYER_BOUND;

    public PlayerStorageToggleButton(int slot, PlayerStorageToggle toggle) {
        this.slot = slot;
        this.toggle = toggle;
    }

    @Override
    public boolean isEnabled(Player player) {
        CreativePlayer cp = (CreativePlayer) player;
        CreativePlayerStorage storage = cp.getCreativeStorage();
        return toggle.isEnabled().apply(storage);
    }

    @Override
    public void setEnabled(Player player, boolean enabled) {
        CreativePlayer cp = (CreativePlayer) player;
        CreativePlayerStorage storage = cp.getCreativeStorage();
        toggle.setEnabled().accept(storage, enabled);
    }

    @Override
    public void onValueChange(Player p, boolean newValue) {
        CreativePlayer player = (CreativePlayer) p;
        sendToggleChangeFx(player, toggle, newValue);
        ChestMenuInv.rerenderButton(slot, p);
    }

    public static void sendToggleChangeFx(CreativePlayer player, PlayerStorageToggle toggle, boolean newValue) {
        String name = toggle.name();
        Component msg = newValue
                ? MM."<gray>Toggled \{name} to <green>ON<gray>!"
                : MM."<gray>Toggled \{name} to <red>OFF<gray>!";

        dataType.sendPit(player, msg);

        double pitch = newValue ? 1.9 : 1.5;
        player.playSound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, pitch);
    }

    @Override
    public Material getIconMaterial(boolean enabled) {
        return toggle.iconMat();
    }

    @Override
    public String getTextIcon() {
        return toggle.iconText();
    }

    @Override
    public String getName() {
        return toggle.name();
    }

    @Override
    public List<Component> getDescription() {
        List<Component> lore = new ArrayList<>();
        lore.add(dataType.getLoreSubtitle());
        if (toggle.commandLabel() != null) {
            lore.add(MM."<cmd>\{toggle.commandLabel()}");
        }
        lore.add(Component.empty());
        lore.addAll(toggle.lore());
        return lore;
    }
}
