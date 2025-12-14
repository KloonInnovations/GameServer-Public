package io.kloon.gameserver.modes.creative.tools.impl.waypoint.menu.manage;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointStorage;
import io.kloon.gameserver.modes.creative.tools.impl.waypoint.menu.WaypointMenu;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WaypointTeleportButton implements ChestButton {
    private final WaypointStorage waypoint;

    public WaypointTeleportButton(WaypointStorage waypoint) {
        this.waypoint = waypoint;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;
        p.closeInventory();
        waypoint.teleport(player);
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        Component name = MM."<title>Teleport";

        List<Component> lore = new ArrayList<>();
        lore.add(MM."<can_undo>");
        lore.add(Component.empty());
        lore.addAll(WaypointMenu.getWaypointInfo(waypoint, player));
        lore.add(Component.empty());
        lore.add(MM."<cta>Click to teleport!");

        Material icon = waypoint.getColor().getMaterial();
        return MenuStack.of(icon).name(name).lore(lore).build();
    }
}
