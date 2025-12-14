package io.kloon.gameserver.modes.creative.storage.playerdata;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.commands.preferences.FlySpeedCommand;
import io.kloon.gameserver.modes.creative.commands.preferences.WalkSpeedCommand;
import io.kloon.gameserver.modes.creative.storage.playerdata.clipboard.PlayerClipboard;
import io.kloon.gameserver.modes.creative.tools.hand.PlayerChangesByHand;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.SnipePlayerStorage;
import io.kloon.infra.mongo.accounts.KloonAccount;
import io.kloon.infra.mongo.storage.BufferedDocument;

public class CreativePlayerStorage {
    private final BufferedDocument document;

    private final ToolsStorage tools;
    private final SelectionColors selectionColors;
    private final PlayerClipboard clipboard;
    private final SnipePlayerStorage snipe;
    private final MessagingStorage messaging;

    public CreativePlayerStorage(KloonAccount account, BufferedDocument document) {
        this.document = document;

        document.putObjectId(ACCOUNT_ID, account.getId());

        this.tools = new ToolsStorage(document.getOrCreateSub(TOOLS));
        this.selectionColors = document.getOrCreateSub(SELECTION_COLORS, doc -> new SelectionColors(account.getMinecraftUuid(), doc));
        this.clipboard = document.getOrCreateSub(CLIPBOARD, doc -> new PlayerClipboard(account, doc));
        this.snipe = document.getOrCreateSub(SNIPE, SnipePlayerStorage::new);
        this.messaging = document.getOrCreateSub(MESSAGING, MessagingStorage::new);
    }

    public BufferedDocument getDocument() {
        return document;
    }

    public ToolsStorage getTools() {
        return tools;
    }

    public InventoryStorage getInventory(CreativePlayer player) {
        BufferedDocument subDoc = document.getOrCreateSub(INVENTORY);
        return new InventoryStorage(subDoc, player);
    }

    public float getFlySpeed() {
        return (float) document.getDouble(FLY_SPEED, FlySpeedCommand.DEFAULT_FLY_SPEED);
    }

    public void setFlySpeed(float flySpeed) {
        document.putDouble(FLY_SPEED, flySpeed);
    }

    public float getWalkSpeed() {
        return (float) document.getDouble(WALK_SPEED, WalkSpeedCommand.DEFAULT_WALK_SPEED);
    }

    public void setWalkSpeed(float walkSpeed) {
        document.putDouble(WALK_SPEED, walkSpeed);
    }

    public int getSpeedEnchant() {
        return document.getInt(SPEED_ENCHANT, -1);
    }

    public void setSpeedEnchant(int amplifier) {
        document.putInt(SPEED_ENCHANT, amplifier);
    }

    public boolean hasNightVision() {
        return document.getBoolean(NIGHT_VISION, false);
    }

    public void setNightVision(boolean nightVision) {
        document.putBoolean(NIGHT_VISION, nightVision);
    }

    public int getHandBufferingTicks() {
        return document.getInt(HAND_BUFFERING_TICKS, PlayerChangesByHand.DEFAULT_DURATION_TICKS);
    }

    public void setHandBufferingTicks(int ticks) {
        document.putInt(HAND_BUFFERING_TICKS, ticks);
    }

    public SelectionColors getSelectionColors() {
        return selectionColors;
    }

    public boolean isRenderingWorldBorder() {
        return document.getBoolean(RENDER_WORLD_BORDER, false);
    }

    public void setRenderingWorldBorder(boolean rendering) {
        document.putBoolean(RENDER_WORLD_BORDER, rendering);
    }

    public boolean isSpawningOnLastLocation() {
        return document.getBoolean(SPAWN_LAST_LOCATION, true);
    }

    public void setSpawningOnLastLocation(boolean spawningOnLastLocation) {
        document.putBoolean(SPAWN_LAST_LOCATION, spawningOnLastLocation);
    }

    public PlayerClipboard getClipboard() {
        return clipboard;
    }

    public double getPastingRange() {
        return document.getDouble(PASTING_RANGE, 4.0);
    }

    public void setPastingRange(double aimRange) {
        document.putDouble(PASTING_RANGE, aimRange);
    }

    public SnipePlayerStorage getSnipe() {
        return snipe;
    }

    public MessagingStorage getMessaging() {
        return messaging;
    }

    public static final String ACCOUNT_ID = "accountId";
    private static final String TOOLS = "tools";
    private static final String INVENTORY = "inventory";
    private static final String FLY_SPEED = "fly_speed";
    private static final String WALK_SPEED = "walk_speed";
    private static final String SPEED_ENCHANT = "speed_enchant";
    private static final String NIGHT_VISION = "night_vision";
    private static final String HAND_BUFFERING_TICKS = "hand_buffering";
    private static final String SELECTION_COLORS = "selection_colors";
    private static final String RENDER_WORLD_BORDER = "render_world_border";
    private static final String SPAWN_LAST_LOCATION = "spawn_last_location";
    private static final String CLIPBOARD = "clipboard";
    private static final String PASTING_RANGE = "pasting_range";
    private static final String SNIPE = "snipe";
    private static final String MESSAGING = "messaging";
}
