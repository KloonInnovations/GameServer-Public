package io.kloon.gameserver.modes.creative.selection;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.selection.rendering.DisplayCuboid;
import io.kloon.gameserver.modes.creative.selection.snap.BlockSelectionUtils;
import io.kloon.gameserver.modes.creative.storage.datainworld.SelectionCuboidStorage;
import io.kloon.gameserver.modes.creative.storage.datainworld.minestom.StorageVec;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;

public final class OneCuboidSelection implements CuboidSelection {
    private final CreativePlayer player;
    private final Vec pos1;

    private final DisplayCuboid selectCuboid;

    public OneCuboidSelection(CreativePlayer player, Vec pos1, DisplayCuboid selectCuboid) {
        this.player = player;
        this.pos1 = pos1;
        this.selectCuboid = selectCuboid;
        selectCuboid.withGlowColor(player.getCreativeStorage().getSelectionColors().getOneSelection());
    }

    public Vec getPos1() {
        return pos1;
    }

    @Override
    public void tick() {
        if (player.isHoldingTool(CreativeToolType.SELECTION)) {
            BoundingBox boundingBox = getHoverBoundingBox(player, pos1);
            selectCuboid.withInterpolation(true).adjust(boundingBox);
        }
    }

    public OneCuboidSelection selectFirst(Vec pos1) {
        return new OneCuboidSelection(player, pos1, selectCuboid);
    }

    public TwoCuboidSelection selectSecond(Vec pos2) {
        return new TwoCuboidSelection(player, pos1, pos2, selectCuboid);
    }

    @Override
    public void remove() {
        if (selectCuboid != null) {
            selectCuboid.remove();
        }
    }

    public Color getHighlightColor() {
        return player.getCreativeStorage().getSelectionColors().getOneSelection();
    }

    @Override
    public SelectionCuboidStorage toStorage() {
        return new SelectionCuboidStorage(new StorageVec(pos1), null);
    }

    public static BoundingBox getHoverBoundingBox(CreativePlayer player, Vec pos1) {
        Vec pos2 = player.getSnipe().computeTarget().asVec();

        Vec corner1 = pos1.min(pos2);
        Vec corner2 = pos1.max(pos2).add(1, 1, 1);
        Vec dimensions = corner2.sub(corner1);

        return new BoundingBox(dimensions.x(), dimensions.y(), dimensions.z(), corner1);
    }

    public static OneCuboidSelection spawn(CreativePlayer player, Vec pos1) {
        BoundingBox boundingBox = getHoverBoundingBox(player, pos1);
        DisplayCuboid cuboid = DisplayCuboid.spawn(player.getInstance(), boundingBox);
        return new OneCuboidSelection(player, pos1, cuboid);
    }
}
