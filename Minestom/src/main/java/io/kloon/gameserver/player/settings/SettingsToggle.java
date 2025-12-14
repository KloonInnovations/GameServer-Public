package io.kloon.gameserver.player.settings;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.player.KloonPlayer;
import io.kloon.gameserver.player.settings.toggle.PlayerToggle;
import io.kloon.gameserver.player.settings.toggle.PlayerToggleButton;
import net.minestom.server.item.Material;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class SettingsToggle extends PlayerToggle<PlayerSettingsStorage> {
    public SettingsToggle(boolean defaultValue, Material iconMat, String iconText, String name, Lore lore,
                          Function<PlayerSettingsStorage, Boolean> isEnabled,
                          BiConsumer<PlayerSettingsStorage, Boolean> setEnabled) {
        super(defaultValue, iconMat, iconText, name, lore, KloonPlayer::getSettingsStorage, isEnabled, setEnabled);
    }

    public PlayerToggleButton<PlayerSettingsStorage> toButton(int slot) {
        return new PlayerToggleButton<>(slot, this);
    }
}
