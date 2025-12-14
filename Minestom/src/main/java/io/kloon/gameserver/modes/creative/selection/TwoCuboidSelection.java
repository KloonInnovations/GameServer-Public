package io.kloon.gameserver.modes.creative.selection;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.minestom.sounds.Pitch;
import io.kloon.gameserver.minestom.utils.BoundingBoxUtils;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.selection.rendering.DisplayCuboid;
import io.kloon.gameserver.modes.creative.storage.datainworld.SelectionCuboidStorage;
import io.kloon.gameserver.modes.creative.storage.datainworld.minestom.StorageVec;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.impl.selection.expander.SelectionExpanderTool;
import io.kloon.gameserver.util.physics.Collisions;
import io.kloon.gameserver.util.coordinates.CardinalDirection;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class TwoCuboidSelection implements CuboidSelection {
    private static final Logger LOG = LoggerFactory.getLogger(TwoCuboidSelection.class);

    private final CreativePlayer player;

    private final Vec pos1;
    private final Vec pos2;

    private final DisplayCuboid selectCuboid;
    private Vec highlightedFace;
    private CreativeTool heldTool;

    public TwoCuboidSelection(CreativePlayer player, Vec pos1, Vec pos2, DisplayCuboid selectCuboid) {
        this.player = player;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.selectCuboid = selectCuboid;

        selectCuboid.withGlowColor(getHighlightColor());
        BoundingBox boundingBox = getBoundingBox(pos1, pos2);
        selectCuboid.withInterpolation(true).adjust(boundingBox);
    }

    public Vec getPos1() {
        return pos1;
    }

    public Vec getPos2() {
        return pos2;
    }

    public BoundingBox getCuboid() {
        BoundingBox boundingBox = BoundingBoxUtils.fromPoints(pos1, pos2);
        return boundingBox.expand(1, 1, 1).withOffset(boundingBox.relativeStart());
    }

    public interface SelectionHighlighter {
        Color getSelectionHighlightColor();

        SoundEvent getHighlightSound();

        @Nullable
        Vec raycastFaceToHighlight(CreativePlayer player, BoundingBox boundingBox);
    }

    @Override
    public void tick() {
        ItemStack inHand = player.getItemInMainHand();
        CreativeTool tool = player.getCreative().getToolsListener().get(inHand);

        Vec faceToHighlight = tool instanceof SelectionHighlighter highlight
                ? highlight.raycastFaceToHighlight(player, getCuboid())
                : null;
        boolean sameFace = Objects.equals(faceToHighlight, highlightedFace);
        boolean sameTool = tool instanceof SelectionHighlighter && Objects.equals(heldTool, tool);
        if (sameFace && sameTool) {
            return;
        }

        selectCuboid.withGlowColor(getHighlightColor());
        if (faceToHighlight == null) {
            soundCd();
        } else {
            SelectionHighlighter highlight = (SelectionHighlighter) tool;
            CardinalDirection face = CardinalDirection.fromVec(faceToHighlight);
            selectCuboid.editFaceEntities(face, meta -> meta.setGlowColorOverride(highlight.getSelectionHighlightColor().asRGB()));
            if (soundCd()) {
                player.playSound(highlight.getHighlightSound(), Pitch.base(1.6).addRand(0.08), 0.4);
            }
        }
        highlightedFace = faceToHighlight;
        heldTool = tool;
    }

    private boolean soundCd() {
        return !SelectionExpanderTool.HOVER_SOUND_COOLDOWN.get(player).isOnCooldown();
    }

    @Override
    public void remove() {
        if (selectCuboid != null) {
            selectCuboid.remove();
        }
    }

    public TwoCuboidSelection adjust(Vec pos1, Vec pos2) {
        BoundingBox boundingBox = getBoundingBox(pos1, pos2);
        selectCuboid.adjust(boundingBox);
        return new TwoCuboidSelection(player, pos1, pos2, selectCuboid);
    }

    public NoCuboidSelection straightToNoSelection() {
        return new NoCuboidSelection(player, selectCuboid);
    }

    public OneCuboidSelection backToOneSelection(Vec pos1) {
        return new OneCuboidSelection(player, pos1, selectCuboid);
    }

    public TwoCuboidSelection selectFirst(Vec pos1) {
        return adjust(pos1, pos2);
    }

    public TwoCuboidSelection selectSecond(Vec pos2) {
        return adjust(pos1, pos2);
    }

    public Color getHighlightColor() {
        return player.getCreativeStorage().getSelectionColors().getFullSelection();
    }

    @Override
    public SelectionCuboidStorage toStorage() {
        return new SelectionCuboidStorage(new StorageVec(pos1), new StorageVec(pos2));
    }

    public static TwoCuboidSelection spawn(CreativePlayer player, Vec pos1, Vec pos2) {
        BoundingBox boundingBox = getBoundingBox(pos1, pos2);
        DisplayCuboid cuboid = DisplayCuboid.spawn(player.getInstance(), boundingBox);
        return new TwoCuboidSelection(player, pos1, pos2, cuboid);
    }

    private static BoundingBox getBoundingBox(Vec pos1, Vec pos2) {
        Vec corner1 = pos1.min(pos2);
        Vec corner2 = pos1.max(pos2).add(1, 1, 1);
        Vec dimensions = corner2.sub(corner1);
        return new BoundingBox(dimensions.x(), dimensions.y(), dimensions.z(), corner1);
    }
}
