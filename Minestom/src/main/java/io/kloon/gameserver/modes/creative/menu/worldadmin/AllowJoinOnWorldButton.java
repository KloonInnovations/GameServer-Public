package io.kloon.gameserver.modes.creative.menu.worldadmin;

import io.kloon.gameserver.chestmenus.builtin.ToggleButton;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;

import java.util.List;

public class AllowJoinOnWorldButton extends ToggleButton {
    @Override
    public boolean isEnabled(Player player) {
        return false;
    }

    @Override
    public void setEnabled(Player player, boolean enabled) {

    }

    @Override
    public void onValueChange(Player player, boolean newValue) {

    }

    @Override
    public Material getIconMaterial(boolean enabled) {
        return null;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public List<Component> getDescription() {
        return List.of();
    }
}
