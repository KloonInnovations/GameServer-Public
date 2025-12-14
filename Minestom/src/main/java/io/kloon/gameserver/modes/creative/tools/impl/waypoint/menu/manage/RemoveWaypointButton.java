package io.kloon.gameserver.modes.creative.tools.impl.waypoint.menu.manage;

import humanize.Humanize;
import io.kloon.gameserver.chestmenus.ButtonClick;
import io.kloon.gameserver.chestmenus.ChestButton;
import io.kloon.gameserver.chestmenus.ChestMenu;
import io.kloon.gameserver.chestmenus.util.MenuStack;
import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointStorage;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointsStorage;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.impl.waypoint.changes.RemoveWaypointChange;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.modes.creative.ux.messaging.SentMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;

public class RemoveWaypointButton implements ChestButton {
    private final ChestMenu menu;
    private final WaypointStorage waypoint;

    public RemoveWaypointButton(ChestMenu menu, WaypointStorage waypoint) {
        this.menu = menu;
        this.waypoint = waypoint;
    }

    @Override
    public void clickButton(Player p, ButtonClick click) {
        CreativePlayer player = (CreativePlayer) p;

        Entity waypointEntity = player.getInstance().getEntityByUuid(waypoint.getUuid());
        if (waypointEntity != null) {
            waypointEntity.remove();
        }

        WaypointsStorage waypoints = player.getInstance().getWorldStorage().getWaypoints();
        waypoints.remove(waypoint);

        SentMessage sentMsg = player.broadcast().send(MsgCat.TOOL,
                NamedTextColor.RED, "AND IT'S GONE!", MM."<gray>Removed waypoint <\{waypoint.getTextColor().asHexString()}>\{waypoint.getName()}<gray>!",
                SoundEvent.BLOCK_BEACON_DEACTIVATE, Pitch.base(1.6).addRand(0.4));

        player.addToHistory(CreativeToolType.WAYPOINTS, "<red>Removed Waypoint",
                sentMsg, new RemoveWaypointChange(waypoint));

        menu.reload();
        menu.display(player);
    }

    @Override
    public ItemStack renderButton(Player player) {
        Component name = MM."<red>Remove Waypoint";

        List<Component> lore = new ArrayList<>();
        lore.add(MM."<can_undo>");
        lore.add(Component.empty());
        lore.addAll(MM_WRAP."<gray>Pff... nobody uses it!");
        lore.add(Component.empty());
        if (waypoint.getLastUse() == 0) {
            lore.add(MM."<gray>Last use: <red>Literally never!");
        } else {
            Date date = new Date(waypoint.getLastUse());
            lore.add(MM."<gray>Last use: <yellow>\{Humanize.naturalTime(date)}");
        }
        lore.add(Component.empty());
        lore.add(MM."<cta>Click to remove!");

        return MenuStack.of(Material.LAVA_BUCKET).name(name).lore(lore).build();
    }
}
