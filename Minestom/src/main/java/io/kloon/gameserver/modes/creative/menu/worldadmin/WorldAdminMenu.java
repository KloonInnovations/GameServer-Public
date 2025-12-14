package io.kloon.gameserver.modes.creative.menu.worldadmin;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.autoupdate.AutoUpdateMenu;
import io.kloon.gameserver.chestmenus.util.Lore;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.buildpermits.menu.BuildPermitsMenu;
import io.kloon.gameserver.modes.creative.commands.menus.WorldAdminCommand;
import io.kloon.gameserver.modes.creative.menu.CreativeMainMenu;
import io.kloon.gameserver.modes.creative.menu.preferences.toggles.WorldStorageToggle;
import io.kloon.gameserver.modes.creative.menu.preferences.toggles.WorldStorageToggleButton;
import io.kloon.gameserver.modes.creative.menu.worldadmin.size.WorldSizeMenu;
import io.kloon.gameserver.modes.creative.menu.worldadmin.time.WorldTimeMenu;
import io.kloon.gameserver.modes.creative.menu.worldadmin.weather.WeatherMenu;
import io.kloon.gameserver.modes.creative.storage.datainworld.CreativeWorldStorage;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class WorldAdminMenu extends ChestMenu implements AutoUpdateMenu {
    public static final String ICON = "\uD83D\uDDFA"; // 🗺

    public static final WorldStorageToggle CAN_JOIN_WORLD = new WorldStorageToggle(
            Material.WARPED_FENCE_GATE, "<green>\uD83D\uDEA1", "Can Join World", // 🚡
            MM_WRAP."<gray>Whether players can /join this specific world.",
            null,
            CreativeWorldStorage::canCommandJoin, CreativeWorldStorage::setCanCommandJoin);

    private final CreativeMainMenu parent;

    public WorldAdminMenu(CreativeMainMenu parent) {
        super("World Administration", ChestSize.SIX);
        this.parent = parent;
    }

    @Override
    protected void registerButtons() {
        reg(11, new BuildPermitsMenu(this, parent.getPlayer()));
        reg(13, slot -> new WorldStorageToggleButton(slot, CAN_JOIN_WORLD));
        reg(15, KickGuestsFromWorldButton::new);

        reg(29, new WorldTimeMenu(this));
        reg(31, new WeatherMenu(this));
        reg(33, new WorldSizeMenu(this));

        reg().goBack(parent);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<light_purple>\{ICON} <title>World Admin";

        Lore lore = new Lore();
        lore.add(MM."<cmd>\{WorldAdminCommand.LABEL}");
        lore.addEmpty();
        lore.wrap("<gray>Manage this world and its instance's settings and players.");
        lore.addEmpty();
        lore.add(MM."<cta>Click to manage world!");

        return MenuStack.of(Material.DECORATED_POT, name, lore);
    }

    @Override
    public boolean shouldReloadMenu() {
        return false;
    }
}
