package io.kloon.gameserver.player.settings.toggle;

import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.player.KloonPlayer;
import net.minestom.server.item.Material;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class PlayerToggle<Storage> {
    private final boolean defaultValue;
    private final Material iconMat;
    private final String iconText;
    private final String name;
    private final Lore lore;
    private final Function<KloonPlayer, Storage> getStorage;
    private final Function<Storage, Boolean> isEnabled;
    private final BiConsumer<Storage, Boolean> setEnabled;

    public PlayerToggle(
            boolean defaultValue, Material iconMat, String iconText, String name, Lore lore,
            Function<KloonPlayer, Storage> getStorage,
            Function<Storage, Boolean> isEnabled,
            BiConsumer<Storage, Boolean> setEnabled
    ) {
        this.defaultValue = defaultValue;
        this.iconMat = iconMat;
        this.iconText = iconText;
        this.name = name;
        this.lore = lore;
        this.getStorage = getStorage;
        this.isEnabled = isEnabled;
        this.setEnabled = setEnabled;
    }

    public boolean defaultValue() {
        return defaultValue;
    }

    public Material iconMat() {
        return iconMat;
    }

    public String iconText() {
        return iconText;
    }

    public String name() {
        return name;
    }

    public Lore lore() {
        return lore;
    }

    public Function<KloonPlayer, Storage> getStorage() {
        return getStorage;
    }

    public Function<Storage, Boolean> isEnabled() {
        return isEnabled;
    }

    public BiConsumer<Storage, Boolean> setEnabled() {
        return setEnabled;
    }
}
