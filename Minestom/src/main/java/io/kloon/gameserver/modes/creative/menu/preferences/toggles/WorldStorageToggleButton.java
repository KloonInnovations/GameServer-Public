package io.kloon.gameserver.modes.creative.menu.preferences.toggles;

import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.builtin.ToggleButton;
import io.kloon.gameserver.creative.storage.owner.WorldOwnerStorage;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.storage.datainworld.CreativeWorldStorage;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WorldStorageToggleButton extends ToggleButton {
    private final int slot;
    private final WorldStorageToggle toggle;

    public WorldStorageToggleButton(int slot, WorldStorageToggle toggle) {
        this.slot = slot;
        this.toggle = toggle;
    }

    @Override
    public boolean isEnabled(Player player) {
        CreativePlayer cp = (CreativePlayer) player;
        CreativeWorldStorage storage = cp.getInstance().getWorldStorage();
        return toggle.isEnabled().apply(storage);
    }

    @Override
    public void setEnabled(Player player, boolean enabled) {
        CreativePlayer cp = (CreativePlayer) player;

        WorldOwnerStorage ownership = cp.getInstance().getWorldDef().ownership();
        if (!ownership.isOwner(cp)) {
            cp.sendPit(NamedTextColor.RED, "CAN'T TOGGLE!", MM."<gray>You aren't world owner!");
            return;
        }

        CreativeWorldStorage storage = cp.getInstance().getWorldStorage();
        toggle.setEnabled().accept(storage, enabled);
    }

    @Override
    public void onValueChange(Player player, boolean newValue) {
        CreativePlayer cp = (CreativePlayer) player;
        sendToggleChangeFx(cp, toggle, newValue);
        ChestMenuInv.rerenderButton(slot, cp);
    }

    public static void sendToggleChangeFx(CreativePlayer player, WorldStorageToggle toggle, boolean newValue) {
        String name = toggle.name();
        Component msg = newValue
                ? MM."<gray>Toggled \{name} to <green>ON<gray>!"
                : MM."<gray>Toggled \{name} to <red>OFF<gray>!";

        player.broadcast().send(MsgCat.WORLD, TextColor.color(59, 247, 103), "WORLD SETTING!", msg);

        double pitch = newValue ? 1.7 : 1.4;
        player.playSound(SoundEvent.BLOCK_NOTE_BLOCK_CHIME, pitch);
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
        //lore.add(MM."<dark_gray>World Setting");
        if (toggle.commandLabel() != null) {
            lore.add(MM."<cmd>\{toggle.commandLabel()}");
            lore.add(Component.empty());
        }
        lore.addAll(toggle.lore());
        return lore;
    }
}
