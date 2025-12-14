package io.kloon.gameserver.modes.creative.tools.impl.waypoint.menu;

import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.ChestSize;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.utils.PointFmt;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.tools.WaypointsCommand;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointColor;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointStorage;
import io.kloon.gameserver.modes.creative.tools.impl.waypoint.menu.manage.*;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WaypointMenu extends ChestMenu {
    private final ChestMenu parent;
    private final WaypointStorage waypoint;

    public WaypointMenu(ChestMenu parent, WaypointStorage waypoint) {
        super(waypoint.getName(), ChestSize.FOUR);
        this.parent = parent;
        this.waypoint = waypoint;

        setTitleFunction(p -> MM."Waypoint: \{waypoint.getName()}");
    }

    @Override
    protected void registerButtons() {
        reg(11, new WaypointTeleportButton(waypoint));
        reg(13, new RenameWaypointButton(this, waypoint));
        reg(15, new RemoveWaypointButton(parent, waypoint));

        reg().goBack(parent);
        reg(size.minus(3), slot -> new ResetWaypointPitchButton(slot, waypoint));

        reg(size.bottomCenter() + 1, slot -> new ToggleWorldSpawnButton(waypoint, slot));
    }

    @Override
    public void clickButton(Player player, ButtonClick click) {
        if (click.isRightClick()) {
            player.closeInventory();
            waypoint.teleport((CreativePlayer) player);
            return;
        }

        super.clickButton(player, click);
    }

    @Override
    public ItemStack renderButton(Player p) {
        CreativePlayer player = (CreativePlayer) p;

        WaypointColor wpColor = waypoint.getColor();

        TextColor textColor = wpColor.getTextColor();
        Component name = MM."<\{textColor.asHexString()}>\{waypoint.getName()}";

        List<Component> lore = new ArrayList<>();
        lore.add(MM."<cmd>\{WaypointsCommand.LABEL_SHORT} \{waypoint.getName()}");
        lore.add(Component.empty());
        lore.addAll(getWaypointInfo(waypoint, player));
        lore.add(Component.empty());

        if (player.getUuid().equals(waypoint.getOwnerId())) {
            lore.add(MM."<gray>Placed by: <green>You!");
        } else {
            lore.add(MM."<gray>Placed by: <white>\{waypoint.getOwnerName()}");
        }
        lore.add(Component.empty());

        lore.add(MM."<rcta>Click to teleport!");
        lore.add(MM."<lcta>Click to manage!");

        return MenuStack.of(wpColor.getMaterial()).name(name).lore(lore).build();
    }

    public static List<Component> getWaypointInfo(WaypointStorage waypoint, CreativePlayer player) {
        Pos pos = waypoint.getPosition();
        NumberFormat noDec = NumberFmt.NO_DECIMAL;

        List<Component> lore = new ArrayList<>();
        lore.add(MM."<gray>Position: <aqua>\{PointFmt.fmt10k(pos)}");
        lore.add(MM."<gray>Rotation: <green>\{noDec.format(pos.yaw())} yaw<gray>, <light_purple>\{noDec.format(pos.pitch())} pitch");

        CreativeInstance instance = player.getInstance();

        double distFromPlayer = pos.distance(player.getPosition());
        double distFromCenter = pos.distance(instance.getWorldCenter());
        lore.add(MM."<dark_gray>\{NumberFmt.ONE_DECIMAL.format(distFromPlayer)} blocks from you");
        lore.add(MM."<dark_gray>\{NumberFmt.ONE_DECIMAL.format(distFromCenter)} blocks from world center");
        return lore;
    }
}
