package io.kloon.gameserver.modes.creative.tools.impl.selection.regular;

import io.kloon.gameserver.minestom.sounds.CoolSound;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.CreativeInstance;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.history.builtin.SelectionChange;
import io.kloon.gameserver.modes.creative.selection.CuboidSelection;
import io.kloon.gameserver.modes.creative.selection.NoCuboidSelection;
import io.kloon.gameserver.modes.creative.selection.OneCuboidSelection;
import io.kloon.gameserver.modes.creative.selection.TwoCuboidSelection;
import io.kloon.gameserver.modes.creative.storage.datainworld.SelectionCuboidStorage;
import io.kloon.gameserver.modes.creative.storage.playerdata.SelectionColors;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import io.kloon.gameserver.modes.creative.tools.TickingTool;
import io.kloon.gameserver.modes.creative.tools.click.ToolClick;
import io.kloon.gameserver.modes.creative.tools.click.ToolClickSide;
import io.kloon.gameserver.modes.creative.tools.click.impl.BlockToolClick;
import io.kloon.gameserver.modes.creative.tools.data.ToolDataDef;
import io.kloon.gameserver.modes.creative.tools.impl.selection.regular.commands.DeselectCommand;
import io.kloon.gameserver.modes.creative.tools.impl.selection.regular.commands.PosCommand;
import io.kloon.gameserver.modes.creative.tools.menus.ItemRef;
import io.kloon.gameserver.modes.creative.ux.messaging.MsgCat;
import io.kloon.gameserver.modes.creative.ux.messaging.SentMessage;
import io.kloon.gameserver.util.physics.Collisions;
import io.kloon.gameserver.util.physics.Ray;
import io.kloon.gameserver.util.coordinates.BoxCorner;
import io.kloon.gameserver.util.input.InputFmt;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static io.kloon.gameserver.MiniMessageTemplate.MM;
import static io.kloon.gameserver.MiniMessageTemplate.MM_WRAP;
import static io.kloon.gameserver.minestom.utils.PointFmt.fmt10k;
import static io.kloon.gameserver.modes.creative.tools.impl.selection.regular.SelectionTool.Settings;
import static io.kloon.gameserver.modes.creative.tools.impl.selection.regular.SelectionTool.Preferences;

public class SelectionTool extends CreativeTool<Settings, Preferences> implements TickingTool {
    public SelectionTool() {
        super(CreativeToolType.SELECTION, new ToolDataDef<>(Settings::new, Settings.class, Preferences::new, Preferences.class));
    }

    @Override
    protected void handleUse(CreativePlayer player, ToolClick click) {
        if (click.side() == ToolClickSide.LEFT && handleClickCorner(player)) {
            return;
        }  else if (click.side() == ToolClickSide.LEFT && click instanceof BlockToolClick blockClick) {
            handleClickBlock(player, blockClick.getBlockPos());
        } else if (click.side() == ToolClickSide.RIGHT) {
            handleRightClick(player);
        }
    }

    private void handleClickBlock(CreativePlayer player, Vec blockPos) {
        CreativeInstance instance = player.getInstance();
        Preferences playerData = getPlayerBound(player);

        CuboidSelection selection = player.getSelection();
        SelectionCuboidStorage before = player.getSelection().toStorage();

        SelectionColors selectionColors = player.getCreativeStorage().getSelectionColors();
        TextColor color1 = TextColor.color(selectionColors.getOneSelection());
        TextColor color2 = TextColor.color(selectionColors.getFullSelection());

        if (selection instanceof NoCuboidSelection noSelection) {
            if (!instance.isInBounds(blockPos)) {
                player.sendOutOfBoundsMessage();
                return;
            }
            player.setSelection(noSelection.selectFirst(blockPos));

            SentMessage msg = player.msg().send(MsgCat.TOOL,
                    color1, "select", MM."<gray>Pos #1 at <\{color1.asHexString()}>\{fmt10k(blockPos)}<gray>!",
                    SoundEvent.BLOCK_NOTE_BLOCK_COW_BELL, 1);
            player.addToHistory(toolType, "<aqua>Adjusted Selection", msg, new SelectionChange(before, player));
        } else if (selection instanceof OneCuboidSelection oneSelection) {
            if (!instance.isInBounds(blockPos)) {
                player.sendOutOfBoundsMessage();
                return;
            }
            player.setSelection(oneSelection.selectSecond(blockPos));

            SentMessage msg = player.msg().send(MsgCat.TOOL,
                    color2, "select", MM."<gray>Pos #2 at <\{color2.asHexString()}>\{fmt10k(blockPos)}<gray>!",
                    SoundEvent.BLOCK_NOTE_BLOCK_COW_BELL, 1.5);
            player.addToHistory(toolType, "<aqua>Adjusted Selection", msg, new SelectionChange(before, player));
        }

        if (selection instanceof TwoCuboidSelection twoSelection && playerData.isAutoDeselect()) {
            player.setSelection(twoSelection.straightToNoSelection());
            player.addToHistory(toolType, "<aqua>Adjusted Selection", MM."<red>Auto-deselected!",
                    new CoolSound(SoundEvent.BLOCK_NOTE_BLOCK_COW_BELL), new SelectionChange(before, player));
            handleClickBlock(player, blockPos);
        }
    }

    // returns true if cancel remaining execution
    private boolean handleClickCorner(CreativePlayer player) {
        if (!(player.getSelection() instanceof TwoCuboidSelection selection)) {
            return false;
        }

        BoxCorner hoveredCorner = computeHoveredCorner(player);
        if (hoveredCorner == null) {
            return false;
        }

        SelectionCuboidStorage selectionBefore = player.getSelection().toStorage();

        BoundingBox renderedCuboid = selection.getCuboid();
        BoundingBox blocksCuboid = renderedCuboid.contract(1, 1, 1).withOffset(renderedCuboid.relativeStart());
        Vec hoveredPos = hoveredCorner.onBox(blocksCuboid);

        BoxCorner opposite = hoveredCorner.opposite();
        Vec pos1 = opposite.onBox(blocksCuboid);
        OneCuboidSelection selectionAfter = selection.backToOneSelection(pos1);
        player.setSelection(selectionAfter);

        TextColor textColor = TextColor.color(selectionAfter.getHighlightColor());
        SentMessage msg = player.msg().send(MsgCat.TOOL,
                textColor, "resizing", MM."<gray>Corner-grabbed pos at \{fmt10k(hoveredPos)}<gray>!",
                SoundEvent.BLOCK_NOTE_BLOCK_COW_BELL, 1.0);

        player.addToHistory(toolType, "<aqua>Adjusted Selection", msg, new SelectionChange(selectionBefore, player));

        return true;
    }

    private void handleRightClick(CreativePlayer player) {
        deselect(player);
    }

    public void deselect(CreativePlayer player) {
        CuboidSelection selection = player.getSelection();
        SelectionCuboidStorage before = player.getSelection().toStorage();

        if (selection instanceof OneCuboidSelection || selection instanceof TwoCuboidSelection) {
            player.sendMessage(MM."<b><dark_red>DESELECT!");
            player.playSound(SoundEvent.BLOCK_NOTE_BLOCK_COW_BELL, 0.5, 1.0);

            selection.remove();
            player.setSelection(new NoCuboidSelection(player));

            player.addToHistory(toolType, "<aqua>Adjusted Selection",
                    MM."<b><dark_red>DESELECT!", new CoolSound(SoundEvent.BLOCK_NOTE_BLOCK_COW_BELL, 0.5),
                    new SelectionChange(before, player));
        }
    }

    public void setPos1(CreativePlayer player) {
        Vec pos = Vec.fromPoint(player.getPosition()).apply(Vec.Operator.FLOOR);

        CreativeInstance instance = player.getInstance();
        if (!instance.isInBounds(pos)) {
            player.sendOutOfBoundsMessage();
            return;
        }

        CuboidSelection selection = player.getSelection();
        SelectionCuboidStorage before = player.getSelection().toStorage();

        SelectionColors selectionColors = player.getCreativeStorage().getSelectionColors();
        TextColor color1 = TextColor.color(selectionColors.getOneSelection());

        if (selection instanceof NoCuboidSelection noSelection) {
            player.setSelection(noSelection.selectFirst(pos));
        } else if (selection instanceof OneCuboidSelection oneSelection) {
            player.setSelection(oneSelection.selectFirst(pos));
        } else if (selection instanceof TwoCuboidSelection twoSelection) {
            player.setSelection(twoSelection.selectFirst(pos));
        }

        SentMessage msg = player.msg().send(MsgCat.TOOL,
                color1, "select", MM."<gray>Pos #1 on you at <\{color1.asHexString()}>\{fmt10k(pos)}<gray>!",
                SoundEvent.BLOCK_NOTE_BLOCK_COW_BELL, 1);
        player.addToHistory(toolType, "<aqua>Adjusted Selection", msg, new SelectionChange(before, player));

        updateCornerGrabs(player);
    }

    public void setPos2(CreativePlayer player) {
        Vec pos = Vec.fromPoint(player.getPosition()).apply(Vec.Operator.FLOOR);

        CreativeInstance instance = player.getInstance();
        if (!instance.isInBounds(pos)) {
            player.sendOutOfBoundsMessage();
            return;
        }

        CuboidSelection selection = player.getSelection();
        SelectionCuboidStorage before = player.getSelection().toStorage();

        SelectionColors selectionColors = player.getCreativeStorage().getSelectionColors();
        TextColor color1 = TextColor.color(selectionColors.getOneSelection());
        TextColor color2 = TextColor.color(selectionColors.getFullSelection());

        if (selection instanceof NoCuboidSelection noSelection) {
            Vec firstPos = player.getPosition().asVec().apply(Vec.Operator.FLOOR);
            player.setSelection(noSelection.selectFirst(firstPos));
            player.msg().send(MsgCat.TOOL,
                    color2, "AUTO-SELECT", MM."<gray>Pos #1 on you at <\{color1.asHexString()}>\{fmt10k(pos)}<gray>!");
        }

        if (selection instanceof OneCuboidSelection oneSelection) {
            player.setSelection(oneSelection.selectSecond(pos));
        } else if (selection instanceof TwoCuboidSelection twoSelection) {
            player.setSelection(twoSelection.selectSecond(pos));
        }

        SentMessage msg = player.msg().send(MsgCat.TOOL,
                color2, "select", MM."<gray>Pos #2 on you at <\{color2.asHexString()}>\{fmt10k(pos)}<gray>!",
                SoundEvent.BLOCK_NOTE_BLOCK_COW_BELL, 1.5);
        player.addToHistory(toolType, "<aqua>Adjusted Selection", msg, new SelectionChange(before, player));

        updateCornerGrabs(player);
    }

    @Override
    public void tickHolding(CreativePlayer player, ItemStack item) {
        updateCornerGrabs(player);
    }

    private void updateCornerGrabs(CreativePlayer player) {
        BoxCorner corner = computeHoveredCorner(player);
        CuboidSelection selection = player.getSelection();
        if (corner != null && selection instanceof TwoCuboidSelection sel) {
            BoundingBox cuboid = sel.getCuboid();
            Vec pos = corner.onBox(cuboid);
            player.attachIfAbsent("tool:selection", () -> {
                CornerGrabPreview cornerGrab = new CornerGrabPreview(player, this, cuboid);
                cornerGrab.setInstance(player.getInstance(), pos);
                return cornerGrab;
            });
        }
    }

    @Nullable
    public BoxCorner computeHoveredCorner(CreativePlayer player) {
        CuboidSelection selection = player.getSelection();
        if (!(selection instanceof TwoCuboidSelection sel)) {
            return null;
        }

        Preferences playerBound = getPlayerBound(player);
        if (!playerBound.isUsingResizeAnchors()) {
            return null;
        }

        Ray ray = player.getEyeRay();
        BoundingBox cuboid = sel.getCuboid();

        double volume = BoundingBoxUtils.blocksVolume(cuboid);
        double radius = Math.cbrt(volume) * 0.25;

        return Arrays.stream(BoxCorner.VALUES).map(corner -> {
                    Vec point = corner.onBox(cuboid);
                    Point collision = Collisions.raycastSphereGetPoint(ray, point, radius);
                    return new CornerAndPoint(corner, collision);
                }).filter(cp -> cp.collision != null)
                .min(Comparator.comparingDouble(cp -> cp.collision.distanceSquared(ray.origin())))
                .map(cp -> cp.corner).orElse(null);
    }

    private record CornerAndPoint(BoxCorner corner, @Nullable Point collision) {}

    @Override
    public boolean usesQSnipe(CreativePlayer player) {
        return true;
    }

    @Override
    public List<Command> createCommands() {
        return Arrays.asList(
                new PosCommand(this, "pos1", true),
                new PosCommand(this, "pos2", false),
                new DeselectCommand(this)
        );
    }

    @Override
    public void openSettingsMenu(CreativePlayer player, ItemRef itemRef) {
        new SelectionToolMenu(this, itemRef).display(player);
    }

    @Override
    public void writeUsage(List<Component> lore, Settings settings, Preferences preferences) {
        lore.add(MM."\{InputFmt.LEFT_CLICK_GREEN} <#FF266E><b>SELECT BLOCK");
        lore.addAll(MM_WRAP."<gray>Click a block, then another block. That makes a box, which is your <selection><gray>.");
        lore.add(Component.empty());
        lore.add(MM."\{InputFmt.RIGHT_CLICK_GREEN} <#FF266E><b>DESELECT");
        lore.addAll(MM_WRAP."<gray>Gets rid of the box.");
    }

    public record Settings() {}

    public static class Preferences {
        private boolean autoDeselect = false;
        private boolean usingResizeAnchors = true;

        public boolean isAutoDeselect() {
            return autoDeselect;
        }

        public void setAutoDeselect(boolean autoDeselect) {
            this.autoDeselect = autoDeselect;
        }

        public boolean isUsingResizeAnchors() {
            return usingResizeAnchors;
        }

        public void setUsingResizeAnchors(boolean usingResizeAnchors) {
            this.usingResizeAnchors = usingResizeAnchors;
        }
    }
}
