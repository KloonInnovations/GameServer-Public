package io.kloon.gameserver.modes.creative.menu.worldadmin.weather;

import io.kloon.gameserver.chestmenus.ChestMenuInv;
import io.kloon.gameserver.chestmenus.builtin.ToggleButton;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.world.WeatherCommand;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.Material;

import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class ToggleRainButton extends ToggleButton {
    @Override
    public boolean isEnabled(Player p) {
        CreativePlayer player = (CreativePlayer) p;
        return player.getInstance().getWorldStorage().getWeather().getRainLevel() != 0;
    }

    @Override
    public void setEnabled(Player player, boolean enabled) {
        WeatherCommand.setRaining((CreativePlayer) player, enabled);
    }

    @Override
    public void onValueChange(Player player, boolean newValue) {
        ChestMenuInv.rerender(player);
    }

    @Override
    public Material getIconMaterial(boolean enabled) {
        return enabled ? Material.LIGHT_BLUE_DYE : Material.GRAY_DYE;
    }

    @Override
    public String getName() {
        return "Toggle Rain";
    }

    @Override
    public List<Component> getDescription() {
        return MM_WRAP."<gray>Whether it is raining or not in the world.";
    }
}
