package io.kloon.gameserver.modes.creative.tools.impl.selection.expander;

import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.builtin.SelectionChange;
import io.kloon.gameserver.modes.creative.selection.CuboidSelection;
import io.kloon.gameserver.modes.creative.selection.TwoCuboidSelection;
import io.kloon.gameserver.modes.creative.storage.datainworld.SelectionCuboidStorage;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.click.ToolClickSide;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import io.kloon.gameserver.modes.creative.tools.generics.ToolMessages;
import io.kloon.gameserver.modes.creative.tools.impl.selection.expander.commands.ExpanderItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.selection.expander.menu.SelectionExpanderMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.modes.creative.ux.messaging.SentMessage;
import io.kloon.gameserver.util.physics.Collisions;
import io.kloon.gameserver.util.physics.Ray;
import io.kloon.gameserver.util.CounterOfIdenticalEvents;
import io.kloon.gameserver.util.cooldowns.maps.PlayerTickCooldownMap;
import io.kloon.gameserver.util.coordinates.CardinalDirection;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.color.Color;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.selection.expander.SelectionExpanderTool.*;

public class SelectionExpanderTool extends CreativeTool<Settings, Preferences> implements TwoCuboidSelection.SelectionHighlighter {
    public static Color GLOW_COLOR = new Color(65, 105, 225);
    public static final PlayerTickCooldownMap HOVER_SOUND_COOLDOWN = new PlayerTickCooldownMap(5);

    private final CounterOfIdenticalEvents counter = new CounterOfIdenticalEvents(3, TimeUnit.SECONDS);

    public SelectionExpanderTool() {
        super(CreativeToolType.SELECTION_EXPANDER, new ToolDataDef<>(Settings::new, Settings.class, Preferences::new, Preferences.class));
    }

    @Override
    public Color getSelectionHighlightColor() {
        return GLOW_COLOR;
    }

    @Override
    public SoundEvent getHighlightSound() {
        return SoundEvent.BLOCK_NOTE_BLOCK_COW_BELL;
    }

    @Override
    public Vec raycastFaceToHighlight(CreativePlayer player, BoundingBox boundingBox) {
        Preferences pref = getPlayerBound(player);
        boolean sneaking = player.isSneaking();

        Ray ray = player.getEyeRay();

        Vec face = sneaking && !pref.isOppositeOnSneak()
                ? Collisions.raycastBoxGetFaceNormalFar(ray, boundingBox)
                : Collisions.raycastBoxGetFaceNormal(ray, boundingBox);
        if (face == null) {
            return null;
        }

        if (sneaking && pref.isOppositeOnSneak()) {
            return face.mul(-1);
        }

        return face;
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        CuboidSelection selection = player.getSelection();
        if (!(selection instanceof TwoCuboidSelection completeSelection)) {
            ToolMessages.sendRequireSelection(player);
            return;
        }

        CreativeInstance instance = player.getInstance();
        SelectionCuboidStorage before = player.getSelection().toStorage();

        Settings settings = getItemBound(click);

        BoundingBox cuboid = completeSelection.getCuboid();
        Vec faceNormalVec = raycastFaceToHighlight(player, cuboid);
        if (faceNormalVec == null) {
            player.sendPit(NamedTextColor.RED, "WHAT?", MM."<gray>Click on a selection to use this tool!");
            player.playSound(SoundEvent.ENTITY_BEE_HURT, 1.0);
            return;
        }
        CardinalDirection faceNormalDir = CardinalDirection.fromVec(faceNormalVec);

        Vec pos1 = completeSelection.getPos1();
        Vec pos2 = completeSelection.getPos2();
        CornersOrder order = new CornersOrder(new Corners(pos1, pos2));

        Vec min = pos1.min(pos2);
        Vec max = pos1.max(pos2);

        boolean expand = click.side() == ToolClickSide.RIGHT;
        if (player.isSneaking()) expand = !expand;

        Vec push = faceNormalVec.mul(expand ? 1 : -1).mul(settings.distance);

        if (faceNormalDir.positive()) {
            max = max.add(push);
        } else {
            min = min.add(push);
        }

        Vec diff = max.sub(min);
        if (diff.x() < 0 || diff.y() < 0 || diff.z() < 0) {
            player.sendPit(NamedTextColor.RED, "ZOOP!", MM."<gray>The selection cannot get smaller!");
            player.playSound(SoundEvent.ENTITY_BEE_HURT, 1.4);
            return;
        }

        Corners adjusted = order.apply(min, max);
        if (instance.isOutOfBounds(adjusted.pos1) || instance.isOutOfBounds(adjusted.pos2)) {
            player.sendOutOfBoundsMessage();
            return;
        }

        player.setSelection(completeSelection.adjust(adjusted.pos1, adjusted.pos2));

        HOVER_SOUND_COOLDOWN.get(player).cooldown();

        SentMessage sentMsg;

        CardinalDirection pushDir = CardinalDirection.fromVec(push);
        if (expand) {
            int events = counter.count(player, "expand");
            Component msg = events == 1
                    ? MM."<gray>Selection expanded towards \{pushDir.name()}!"
                    : MM."<gray>Selection expanded towards \{pushDir.name()}! <dark_gray>(\{events})";
            sentMsg = player.msg().send(MsgCat.TOOL, NamedTextColor.BLUE, "EXPAND!", msg,
                    SoundEvent.BLOCK_SCULK_SENSOR_CLICKING, 1.4);
        } else {
            int events = counter.count(player, "reduce");
            Component msg = events == 1
                    ? MM."<gray>Selection contracted towards \{pushDir.name()}!"
                    : MM."<gray>Selection contracted towards \{pushDir.name()}! <dark_gray>(\{events})";
            sentMsg = player.msg().send(MsgCat.TOOL, NamedTextColor.AQUA, "REDUCE!", msg,
                    SoundEvent.BLOCK_SCULK_SENSOR_CLICKING, 1.96);
        }

        player.addToHistory(toolType, "<aqua>Adjusted Selection", sentMsg, new SelectionChange(before, player));
    }

    @Override
    public void writeUsage(List<Component> lore, Settings settings, Preferences preferences) {
        lore.add(MM."\{InputFmt.LEFT_CLICK_GREEN} <#FF266E><b>CONTRACT");
        lore.addAll(MM_WRAP."<gray>Contracts <selection> <gray>based on <blue>highlighted <gray>face.");
        lore.add(Component.empty());
        lore.add(MM."\{InputFmt.RIGHT_CLICK_GREEN} <#FF266E><b>EXPAND");
        lore.addAll(MM_WRAP."<gray>Expands <selection> <gray>towards you.");
        lore.add(Component.empty());
        lore.add(MM."\{InputFmt.SNEAK} Sneaking reverses the face!");
    }

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        new SelectionExpanderMenu(this, itemRef).display(player);
    }

    @Override
    public List<Command> createCommands() {
        return Arrays.asList(

                new ExpanderItemCommand(this)
        );
    }

    private record Corners(Vec pos1, Vec pos2) {
    }

    private record CornersOrder(boolean x, boolean y, boolean z) {
        public CornersOrder(Corners corners) {
            this(
                    corners.pos1.x() < corners.pos2.x(),
                    corners.pos1.y() < corners.pos2.y(),
                    corners.pos1.z() < corners.pos2.z());
        }

        public Corners apply(Vec pos1, Vec pos2) {
            Vec min = pos1.min(pos2);
            Vec max = pos1.max(pos2);

            Vec corner1 = new Vec(
                    x ? min.x() : max.x(),
                    y ? min.y() : max.y(),
                    z ? min.z() : max.z());
            Vec corner2 = new Vec(
                    x ? max.x() : min.x(),
                    y ? max.y() : min.y(),
                    z ? max.z() : min.z());

            return new Corners(corner1, corner2);
        }
    }

    public static class Settings {
        private int distance = 1;

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }
    }

    public static class Preferences {
        private boolean oppositeOnSneak = false;

        public boolean isOppositeOnSneak() {
            return oppositeOnSneak;
        }

        public void setOppositeOnSneak(boolean oppositeOnSneak) {
            this.oppositeOnSneak = oppositeOnSneak;
        }
    }
}
