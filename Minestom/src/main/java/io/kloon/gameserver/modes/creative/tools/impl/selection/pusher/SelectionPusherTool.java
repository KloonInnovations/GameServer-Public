package io.kloon.gameserver.modes.creative.tools.impl.selection.pusher;

import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.minestom.utils.PointFmt;
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
import io.kloon.gameserver.modes.creative.tools.impl.selection.expander.SelectionExpanderTool;
import io.kloon.gameserver.modes.creative.tools.impl.selection.pusher.commands.PushItemCommand;
import io.kloon.gameserver.modes.creative.tools.impl.selection.pusher.commands.PushOrPullCommand;
import io.kloon.gameserver.modes.creative.tools.impl.selection.pusher.menu.SelectionPusherMenu;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.modes.creative.ux.messaging.SentMessage;
import io.kloon.gameserver.util.physics.Collisions;
import io.kloon.gameserver.util.physics.Ray;
import io.kloon.gameserver.util.CounterOfIdenticalEvents;
import io.kloon.gameserver.util.coordinates.CardinalDirection;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.color.Color;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.modes.creative.tools.impl.selection.pusher.SelectionPusherTool.*;

public class SelectionPusherTool extends CreativeTool<Settings, Preferences> implements TwoCuboidSelection.SelectionHighlighter {
    private final CounterOfIdenticalEvents counter = new CounterOfIdenticalEvents(3, TimeUnit.SECONDS);

    public SelectionPusherTool() {
        super(CreativeToolType.SELECTION_PUSHER, new ToolDataDef<>(Settings::new, Settings.class, Preferences::new, Preferences.class));
    }

    @Override
    public Color getSelectionHighlightColor() {
        return new Color(255, 189, 27);
    }

    @Override
    public SoundEvent getHighlightSound() {
        return SoundEvent.BLOCK_NOTE_BLOCK_XYLOPHONE;
    }

    @Override
    public @Nullable Vec raycastFaceToHighlight(CreativePlayer player, BoundingBox boundingBox) {
        return Collisions.raycastBoxGetFaceNormal(player.getEyeRay(), boundingBox);
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        CuboidSelection selection = player.getSelection();
        if (!(selection instanceof TwoCuboidSelection completeSelection)) {
            player.sendPit(NamedTextColor.RED, "WOAH THERE!", MM."<gray>You need a selection before using this tool!");
            player.playSound(SoundEvent.ENTITY_BEE_HURT, 0.8);
            return;
        }

        BoundingBox cuboid = completeSelection.getCuboid();
        Vec faceNormalVec = raycastFaceToHighlight(player, cuboid);
        if (faceNormalVec == null) {
            player.sendPit(NamedTextColor.RED, "WHAT?", MM."<gray>Click on a selection to use this tool!");
            player.playSound(SoundEvent.ENTITY_BEE_HURT, 0.8);
            return;
        }

        CardinalDirection faceNormalDir = CardinalDirection.fromVec(faceNormalVec);

        boolean pushing = click.side() == ToolClickSide.LEFT;
        int distance = getItemBound(click.getItem()).blocks;
        Vec push = faceNormalDir.vec().mul(-distance);
        CardinalDirection pushDir = CardinalDirection.fromVec(push);

        pushSelection(player, pushDir, distance, pushing);
    }

    public void pushSelection(CreativePlayer player, CardinalDirection pushDir, int distance, boolean pushing) {
        if (!(player.getSelection() instanceof TwoCuboidSelection selection)) {
            player.sendPit(NamedTextColor.RED, "WOAH THERE!", MM."<gray>You need a selection before using this tool!");
            player.playSound(SoundEvent.ENTITY_BEE_HURT, 0.8);
            return;
        }

        CreativeInstance instance = player.getInstance();
        SelectionCuboidStorage before = player.getSelection().toStorage();

        Vec oldPos1 = selection.getPos1();
        Vec oldPos2 = selection.getPos2();

        Vec push = pushDir.mul(distance).mul(pushing ? 1 : -1);

        Vec newPos1 = oldPos1.add(push);
        Vec newPos2 = oldPos2.add(push);

        if (instance.isOutOfBounds(newPos1) || instance.isOutOfBounds(newPos2)) {
            player.sendOutOfBoundsMessage();
            return;
        }

        player.setSelection(selection.adjust(newPos1, newPos2));

        SelectionExpanderTool.HOVER_SOUND_COOLDOWN.get(player).cooldown();

        int events = counter.count(player, new MoveEvent(pushing, distance));
        Component msg;
        if (distance == 1) {
            msg = events == 1
                    ? MM."<gray>Selection moved towards \{pushDir.name()}!"
                    : MM."<gray>Selection moved towards \{pushDir.name()}! <dark_gray>(\{events})";
        } else {
            msg = events == 1
                    ? MM."<gray>Selection moved \{distance} blocks towards \{pushDir.name()}!"
                    : MM."<gray>Selection moved \{distance} blocks towards \{pushDir.name()}! <dark_gray>(\{events})";
        }

        SentMessage sentMsg;
        if (pushing) {
            sentMsg = player.msg().send(MsgCat.TOOL,
                    NamedTextColor.GOLD, "PUSH!", msg,
                    SoundEvent.BLOCK_SCULK_CHARGE, Pitch.base(1.4).addRand(0.06));
        } else {
            sentMsg = player.msg().send(MsgCat.TOOL,
                    NamedTextColor.YELLOW, "PULL!", msg,
                    SoundEvent.BLOCK_SCULK_CHARGE,  Pitch.base(1.94).addRand(0.06));
        }

        player.addToHistory(toolType, "<aqua>Adjusted Selection", sentMsg, new SelectionChange(before, player));
    }

    public void moveSelection(CreativePlayer player, Point offset) {
        if (!(player.getSelection() instanceof TwoCuboidSelection selection)) {
            player.sendPit(NamedTextColor.RED, "WOAH THERE!", MM."<gray>You need a selection before using this tool!");
            player.playSound(SoundEvent.ENTITY_BEE_HURT, 0.8);
            return;
        }

        Vec offsetVec = Vec.fromPoint(offset).apply(Vec.Operator.FLOOR);
        CreativeInstance instance = player.getInstance();
        SelectionCuboidStorage before = player.getSelection().toStorage();

        Vec newPos1 = selection.getPos1().add(offset);
        Vec newPos2 = selection.getPos2().add(offset);

        if (instance.isOutOfBounds(newPos1) || instance.isOutOfBounds(newPos2)) {
            player.sendOutOfBoundsMessage();
            return;
        }

        player.setSelection(selection.adjust(newPos1, newPos2));

        SentMessage sentMsg = player.msg().send(MsgCat.TOOL,
                NamedTextColor.GOLD, "PUSH!", MM."<gray>Selection moved by \{PointFmt.fmtVec(offsetVec)}!",
                SoundEvent.BLOCK_SCULK_CHARGE, Pitch.rng(0.6, 0.2));
        player.addToHistory(toolType, "<aqua>Adjusted Selection", sentMsg, new SelectionChange(before, player));
    }

    private record MoveEvent(boolean pushing, int distance) {}

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        new SelectionPusherMenu(this, itemRef).display(player);
    }

    @Override
    public List<Command> createCommands() {
        return Arrays.asList(
                new PushOrPullCommand(this, "push", true),
                new PushOrPullCommand(this, "pull", false),
                new PushItemCommand(this)
        );
    }

    @Override
    public Component renderName(Settings settings) {
        if (settings.blocks == 1) {
            return super.renderName(settings);
        } else {
            return MM."<white>Selection Pusher (\{settings.blocks} blocks)";
        }
    }

    @Override
    public void writeUsage(List<Component> lore, Settings settings, Preferences preferences) {
        lore.add(MM."\{InputFmt.LEFT_CLICK_GREEN} <#FF266E><b>PUSH");
        lore.addAll(MM_WRAP."<gray>Pushes <selection> <gray>towards <gold>highlighted <gray>face.");
        lore.add(Component.empty());
        lore.add(MM."\{InputFmt.RIGHT_CLICK_GREEN} <#FF266E><b>PULL");
        lore.addAll(MM_WRAP."<gray>Same as push, but it's pull.");
    }

    public static class Settings {
        public int blocks = 1;
    }

    public static class Preferences {

    }
}
