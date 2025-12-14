package io.kloon.gameserver.modes.creative.menu.worldadmin.time.preset;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.world.TimeCommand;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class PresetTimeButton implements ChestButton {
    private final PresetTime presetTime;
    private final Material icon;

    public PresetTimeButton(PresetTime presetTime, Material icon) {
        this.presetTime = presetTime;
        this.icon = icon;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        TimeCommand.setTime(player, presetTime.getMcTime());
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<title>\{presetTime.getLabel()}";

        Lore lore = new Lore();
        lore.add(MM."<cmd>\{TimeCommand.LABEL} set \{presetTime.getMcTime()}");
        lore.addEmpty();
        lore.wrap(MM."<gray>\{presetTime.getDescription()}");
        lore.addEmpty();
        lore.add(MM."<gray>Minecraft Time: <green>\{presetTime.getMcTime()}");
        lore.addEmpty();
        lore.add(MM."<yellow>Click to set time!");

        return MenuStack.of(icon, name, lore);
    }
}
