package io.kloon.gameserver.modes.creative.storage.datainworld.waypoints;

import humanize.Humanize;
import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.CreativeMainMenu;
import io.kloon.gameserver.modes.creative.storage.datainworld.minestom.StoragePos;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.impl.teleport.TeleportChange;
import io.kloon.gameserver.modes.creative.tools.impl.waypoint.WaypointEntity;
import io.kloon.gameserver.modes.creative.tools.impl.waypoint.menu.WaypointMenu;
import io.kloon.gameserver.modes.creative.tools.impl.waypoint.menu.WaypointsManagementMenu;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.modes.creative.ux.messaging.SentMessage;
import io.kloon.gameserver.util.formatting.NumberFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.sound.SoundEvent;

import java.util.UUID;

import static io.kloon.gameserver.MiniMessageTemplate.MM;

public class WaypointStorage {
    private UUID uuid;

    private StoragePos position;

    private String name = "Waypoint";

    private UUID ownerId;
    private String ownerName;

    private long timestamp;

    private long lastUse;
    private long uses;

    private boolean worldSpawn;

    private String color;

    public WaypointStorage() {}

    public WaypointStorage(Pos position, Player owner) {
        this(UUID.randomUUID(), position, owner.getUuid(), owner.getUsername());
        this.position = new StoragePos(position);

        this.ownerId = owner.getUuid();
        this.ownerName = owner.getUsername();
    }

    public WaypointStorage(UUID uuid, Pos position, UUID ownerId, String username) {
        this.uuid = uuid;
        this.position = new StoragePos(position);
        this.ownerId = ownerId;
        this.ownerName = username;

        this.timestamp = System.currentTimeMillis();
    }

    public UUID getUuid() {
        return uuid;
    }

    public Pos getPosition() {
        return position.toPos();
    }

    public float getYaw() {
        return position.yaw();
    }

    public WaypointStorage withPosition(Pos position) {
        this.position = new StoragePos(position);
        return this;
    }

    public String getName() {
        return name;
    }

    public String getNameMM() {
        String color = getTextColor().asHexString();
        return STR."<\{color}>\{name}";
    }

    public WaypointStorage withName(String name) {
        this.name = name;
        return this;
    }

    public boolean isWorldSpawn() {
        return worldSpawn;
    }

    public WaypointStorage withWorldSpawn(boolean worldSpawn) {
        this.worldSpawn = worldSpawn;
        return this;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getLastUse() {
        return lastUse;
    }

    public long getUses() {
        return uses;
    }

    public void markUsed() {
        this.lastUse = System.currentTimeMillis();
        this.uses++;
    }

    public WaypointColor getColor() {
        return WaypointColor.BY_DBKEY.get(color, WaypointColor.RED);
    }

    public TextColor getTextColor() {
        return getColor().getTextColor();
    }

    public WaypointStorage withColor(WaypointColor color) {
        this.color = color.getDbKey();
        return this;
    }

    public void openMenu(CreativePlayer player) {
        CreativeMainMenu mainMenu = new CreativeMainMenu(player);
        WaypointsManagementMenu management = new WaypointsManagementMenu(mainMenu, player);
        new WaypointMenu(management, this).display(player);
    }

    public void teleport(CreativePlayer player) {
        Pos posBefore = player.getPosition();
        boolean flyingBefore = player.isFlying();

        player.teleport(getPosition());

        double dist = posBefore.distance(player.getPosition());
        SentMessage msg = player.msg().send(MsgCat.TOOL,
                getTextColor(), "TELEPORTED!", MM."<gray>To waypoint <\{getTextColor().asHexString()}>\{name}<gray> \{NumberFmt.NO_DECIMAL.format(dist)} blocks away!",
                SoundEvent.BLOCK_BEACON_ACTIVATE, Pitch.base(1.7).addRand(0.3));

        markUsed();
        Entity entity = player.getInstance().getEntityByUuid(uuid);
        if (entity instanceof WaypointEntity wpEntity) {
            wpEntity.markUsed(player);
        }

        Component historyText = MM."<gray>Teleport to waypoint \{getNameMM()}<gray>!";
        player.addToHistory(CreativeToolType.WAYPOINTS, STR."<\{getTextColor().asHexString()}>Waypoint teleport",
                historyText, msg.sound(),
                new TeleportChange(posBefore, flyingBefore, player));

        if (isPowerOfTen(uses) && uses > 1) {
            player.sendPit(NamedTextColor.GREEN, "CONGRATS!!!", MM."<gray>You are the \{Humanize.ordinal(uses)} user of this waypoint!");
        }
    }

    private boolean isPowerOfTen(long number) {
        if (number <= 0) return false;

        double log10 = Math.log10(number);
        return log10 == (int) log10;
    }
}
