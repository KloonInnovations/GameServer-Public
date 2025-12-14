package io.kloon.gameserver.modes.creative.tools.impl.waypoint.menu;

import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.builtin.StaticButton;
import io.kloon.gameserver.chestmenus.layout.ChestLayouts;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.tools.WaypointsCommand;
import io.kloon.gameserver.modes.creative.menu.tools.ToolPickupButton;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointColor;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointStorage;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointsStorage;
import io.kloon.gameserver.modes.creative.tools.impl.waypoint.WaypointsTool;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolToggle;
import io.kloon.gameserver.modes.creative.tools.menus.toggles.ToolPreferenceToggleButton;
import io.kloon.gameserver.util.RandUtil;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class WaypointsManagementMenu extends ChestMenu {
    public static final String ICON = "⚐"; // ⚐

    private final ChestMenu parent;
    private final CreativePlayer player;

    public static final ToolToggle<WaypointsTool.Preferences> ALWAYS_SHOW_WAYPOINTS = new ToolToggle<>(
            Material.GLOW_BERRIES, "Always Show Waypoints",
            MM_WRAP."<gray>Regardless of whether you have the tool in hand or not.",
            WaypointsTool.Preferences::isAlwaysShowingWaypoints, WaypointsTool.Preferences::setAlwaysShowingWaypoints);

    public WaypointsManagementMenu(ChestMenu parent, CreativePlayer player) {
        super(STR."\{ICON} Waypoints Management");
        this.parent = parent;
        this.player = player;
    }

    @Override
    protected void registerButtons() {
        WaypointsStorage waypointsStorage = player.getInstance().getWorldStorage().getWaypoints();
        List<WaypointStorage> waypoints = new ArrayList<>(waypointsStorage.getList());
        waypoints.sort(Comparator.comparingLong(WaypointStorage::getTimestamp).reversed());

        if (waypoints.isEmpty()) {
            reg(22, new StaticButton(MenuStack.of(Material.BLACK_STAINED_GLASS)
                    .name(MM."<red>No waypoints!")
                    .lore(MM_WRAP."<gray>Make one, I dare you! (use the tool)")));
        } else {
            ChestLayouts.INSIDE.distribute(waypoints, (slot, waypoint) -> {
                reg(slot, new WaypointMenu(this, waypoint));
            });
        }

        reg().goBack(parent);

        WaypointsTool waypointTool = player.getCreative().getWaypointTool();

        reg(size.bottomCenter() - 1, slot -> new ToolPreferenceToggleButton<>(slot, waypointTool, ALWAYS_SHOW_WAYPOINTS));
        reg(size.bottomCenter() + 1, new ToolPickupButton(waypointTool));
    }

    @Override
    public ItemStack renderButton(Player player) {
        WaypointColor randomColor = RandUtil.getRandom(WaypointColor.LIST);

        String flagColor = randomColor.getTextColor().asHexString();
        Component name = MM."<\{flagColor}>\{ICON} <title>Waypoints";

        List<Component> lore = new ArrayList<>();
        lore.add(MM."<cmd>\{WaypointsCommand.LABEL}");
        lore.add(Component.empty());
        lore.addAll(MM_WRAP."<gray>Set waypoints to fast travel on your world.");
        lore.add(Component.empty());
        lore.add(MM."<cta>Click to manage!");

        return MenuStack.of(randomColor.getMaterial()).name(name).lore(lore).build();
    }
}
