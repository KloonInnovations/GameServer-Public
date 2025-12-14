package io.kloon.gameserver.player.settings.toggle;

import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.builtin.ToggleButton;
import io.kloon.gameserver.player.KloonPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PlayerToggleButton<Storage> extends ToggleButton {
    private final int slot;
    private final PlayerToggle<Storage> toggle;

    private static final TextColor color = TextColor.color(24, 175, 130);

    public PlayerToggleButton(int slot, PlayerToggle<Storage> toggle) {
        this.slot = slot;
        this.toggle = toggle;
    }

    @Override
    public boolean isEnabled(Player p) {
        KloonPlayer player = (KloonPlayer) p;
        Storage storage = toggle.getStorage().apply(player);
        return toggle.isEnabled().apply(storage);
    }

    @Override
    public void setEnabled(Player p, boolean enabled) {
        KloonPlayer player = (KloonPlayer) p;
        Storage storage = toggle.getStorage().apply(player);
        toggle.setEnabled().accept(storage, enabled);
    }

    @Override
    public void onValueChange(Player p, boolean newValue) {
        KloonPlayer player = (KloonPlayer) p;

        Component msg = newValue
                ? MM."<gray>Toggled \{getName()} to <green>ON<gray>!"
                : MM."<gray>Toggled \{getName()} to <red>OFF<gray>!";
        player.sendPit(color, "SETTING!", msg);

        double pitch = newValue ? 1.9 : 1.5;
        player.playSound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, pitch);

        ChestMenuInv.rerenderButton(slot, p);
    }

    @Override
    public Material getIconMaterial(boolean enabled) {
        return toggle.iconMat();
    }

    @Override
    public String getName() {
        return toggle.name();
    }

    @Override
    public List<Component> getDescription() {
        return toggle.lore().asList();
    }
}
