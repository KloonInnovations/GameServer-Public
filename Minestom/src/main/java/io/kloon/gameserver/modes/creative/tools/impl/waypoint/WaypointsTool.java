package io.kloon.gameserver.modes.creative.tools.impl.waypoint;

import com.google.common.collect.*;
import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.minestom.utils.PointFmt;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.tools.WaypointsCommand;
import io.kloon.gameserver.modes.creative.storage.datainworld.CreativeWorldStorage;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointColor;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointStorage;
import io.kloon.gameserver.modes.creative.storage.datainworld.waypoints.WaypointsStorage;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.click.ToolClickSide;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import io.kloon.gameserver.modes.creative.tools.impl.waypoint.changes.AddWaypointChange;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.modes.creative.ux.messaging.SentMessage;
import io.kloon.gameserver.util.physics.Collisions;
import io.kloon.gameserver.util.physics.Ray;
import io.kloon.gameserver.util.RandUtil;
import io.kloon.gameserver.util.formatting.NumberFmt;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.waypoint.WaypointsTool.Preferences;
import static io.kloon.gameserver.modes.creative.tools.impl.waypoint.WaypointsTool.Settings;

public class WaypointsTool extends CreativeTool<Settings, Preferences> {
    private static final int WAYPOINTS_LIMIT = 24;

    public WaypointsTool() {
        super(CreativeToolType.WAYPOINTS, new ToolDataDef<>(Settings::new, Settings.class, Preferences::new, Preferences.class));
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        if (player.isSneaking()) {
            handleSneakClick(player);
        } else if (click.side() == ToolClickSide.LEFT) {
            handleLeftClick(player);
        } else {
            handleRightClick(player);
        }
    }

    private void handleLeftClick(CreativePlayer player) {
        List<WaypointEntity> entities = getWaypointEntities(player.getInstance());
        WaypointEntity clickedWaypointEntity = Collisions.raycastThings(player.getEyeRay(), entities, WaypointEntity::getBoundingBoxForTool);
        if (clickedWaypointEntity == null) {
            return;
        }

        player.playSound(SoundEvent.BLOCK_BEACON_AMBIENT, Pitch.base(1.85).addRand(0.15));

        WaypointStorage clickedWaypoint = clickedWaypointEntity.getStorage();
        clickedWaypoint.openMenu(player);
    }

    private void handleRightClick(CreativePlayer player) {
        CreativeInstance instance = player.getInstance();
        CreativeWorldStorage worldStorage = instance.getWorldStorage();
        WaypointsStorage waypoints = worldStorage.getWaypoints();
        if (waypoints.size() + 1 > WAYPOINTS_LIMIT) {
            player.sendPit(NamedTextColor.RED, "TOO MUCH!", MM."<gray>Can't have more than \{WAYPOINTS_LIMIT} waypoints per world!");
            player.playSound(SoundEvent.ENTITY_VILLAGER_NO, 0.8);
            return;
        }

        Pos position = player.getPosition();
        if (instance.isOutOfBounds(position)) {
            player.sendOutOfBoundsMessage();
            return;
        }

        WaypointColorAndName colorAndName = pickColorAndName(worldStorage);

        WaypointStorage waypointStorage = new WaypointStorage(position, player)
                .withColor(colorAndName.color)
                .withName(colorAndName.name);
        waypoints.add(waypointStorage);
        WaypointEntity.spawn(instance, waypointStorage);

        SentMessage sendMsg = player.broadcast().send(MsgCat.TOOL,
                colorAndName.color().getTextColor(), "WAYPOINT ADDED!", MM."<gray>At \{PointFmt.fmt10k(position)} with \{NumberFmt.NO_DECIMAL.format(position.yaw())} yaw!",
                SoundEvent.BLOCK_BEACON_POWER_SELECT, Pitch.base(1.62).addRand(0.3));

        ItemStack inHand = player.getItemInMainHand();
        player.setItemInMainHand(renderItem(getItemBound(inHand), getPlayerBound(player)));

        Component historyText = MM."<gray>Added waypoint at \{PointFmt.fmt10k(position)}!";
        String hexColor = colorAndName.color.getTextColor().asHexString();
        player.addToHistory(toolType, STR."<\{hexColor}>Waypoint Added", historyText, sendMsg.sound(), new AddWaypointChange(waypointStorage));
    }

    private void handleSneakClick(CreativePlayer player) {
        CreativeInstance instance = player.getInstance();
        CreativeWorldStorage worldStorage = instance.getWorldStorage();
        List<WaypointStorage> waypoints = worldStorage.getWaypoints().getList();
        if (waypoints.isEmpty()) {
            player.sendPit(NamedTextColor.RED, "404 NOT FOUND!", MM."<gray>There are no waypoints to teleport to!");
            player.playSound(SoundEvent.ENTITY_VILLAGER_NO, 0.8);
            return;
        }

        Ray ray = player.getEyeRay();
        WaypointStorage waypoint = waypoints.stream().min(Comparator.comparingDouble(wp -> {
            Pos position = wp.getPosition();
            Point pointOnRay = Collisions.pointOnRay(ray, position);
            return pointOnRay.distanceSquared(position);
        })).orElse(waypoints.getFirst());

        Pos waypointPos = waypoint.getPosition();

        Pos eyePos = player.getEyePosition();
        Vec toWaypoint = Vec.fromPoint(waypointPos.sub(eyePos)).normalize();
        Vec dir = eyePos.direction();
        if (dir.dot(toWaypoint) < 0.2) {
            player.sendPit(NamedTextColor.RED, "NOTHING FOUND!", MM."<gray>There are no waypoints in that direction!");
            player.playSound(SoundEvent.ENTITY_VILLAGER_NO, 0.8);
            return;
        }

        waypoint.teleport(player);
    }

    @Override
    public @Nullable ItemStack renderOverride(Settings settings, Preferences preferences) {
        WaypointColor color = RandUtil.getRandom(WaypointColor.LIST);
        Material material = color.getMaterial();
        return toolBuilder(settings, material).lore(getItemLore(settings, preferences)).build();
    }

    @Override
    public void writeUsage(List<Component> lore, Settings settings, Preferences preferences) {
        lore.add(MM."\{InputFmt.LEFT_CLICK_GREEN} <#FF266E><b>INTERACT");
        lore.addAll(MM_WRAP."<gray>Interact with target waypoint. Can be done from very far.");
        lore.add(Component.empty());
        lore.add(MM."\{InputFmt.RIGHT_CLICK_GREEN} <#FF266E><b>NEW WAYPOINT");
        lore.addAll(MM_WRAP."<gray>Place down a new waypoint exactly where you're standing.");
        lore.add(Component.empty());
        lore.add(MM."\{InputFmt.SNEAK_CLICK_GREEN} <#FF266E><b>TELEPORT");
        lore.addAll(MM_WRAP."<gray>To nearest waypoint in the direction you're aiming.");
    }

    public void spawnWaypoints(CreativeInstance instance) {
        CreativeWorldStorage worldStorage = instance.getWorldStorage();
        Collection<WaypointStorage> waypoints = worldStorage.getWaypoints().getList();
        for (WaypointStorage waypoint : waypoints) {
            if (instance.getEntityByUuid(waypoint.getUuid()) != null) continue;
            WaypointEntity.spawn(instance, waypoint);
        }
    }

    public List<WaypointEntity> getWaypointEntities(CreativeInstance instance) {
        CreativeWorldStorage storage = instance.getWorldStorage();
        return storage.getWaypoints().getList().stream()
                .map(waypointStorage -> (WaypointEntity) instance.getEntityByUuid(waypointStorage.getUuid()))
                .filter(Objects::nonNull).toList();
    }

    private WaypointColorAndName pickColorAndName(CreativeWorldStorage storage) {
        Collection<WaypointStorage> waypoints = storage.getWaypoints().getList();

        Map<WaypointColor, Integer> colorOccurrences = new HashMap<>();
        waypoints.forEach(waypoint -> colorOccurrences.compute(waypoint.getColor(), (_, prev) -> prev == null ? 1 : prev + 1));

        Set<WaypointColor> existingColors = waypoints.stream().map(WaypointStorage::getColor).collect(Collectors.toSet());
        Set<WaypointColor> availableColors = Sets.difference(WaypointColor.SET, existingColors);
        if (availableColors.isEmpty()) {
            availableColors = WaypointColor.SET;
        }

        List<WaypointColor> possibleColors = new ArrayList<>(availableColors);
        WaypointColor color = RandUtil.getRandom(possibleColors);

        int countWithColor = colorOccurrences.getOrDefault(color, 0);
        String name = countWithColor == 0 ? color.getName() : STR."\{color.getName()} #\{countWithColor + 1}";

        return new WaypointColorAndName(color, name);
    }

    record WaypointColorAndName(WaypointColor color, String name) {}



    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        WaypointsCommand.openWaypointsMenu(player);
    }

    public static class Settings {

    }

    public static class Preferences {
        private boolean alwaysShowingWaypoints = false;

        public boolean isAlwaysShowingWaypoints() {
            return alwaysShowingWaypoints;
        }

        public void setAlwaysShowingWaypoints(boolean alwaysShowingWaypoints) {
            this.alwaysShowingWaypoints = alwaysShowingWaypoints;
        }
    }
}
