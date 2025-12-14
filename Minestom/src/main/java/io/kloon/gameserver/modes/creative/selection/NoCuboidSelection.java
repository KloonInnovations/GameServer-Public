package io.kloon.gameserver.modes.creative.selection;

import io.kloon.gameserver.minestom.color.ColorUtils;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.selection.rendering.DisplayCuboid;
import io.kloon.gameserver.modes.creative.selection.snap.BlockSelectionUtils;
import io.kloon.gameserver.modes.creative.storage.datainworld.SelectionCuboidStorage;
import io.kloon.gameserver.modes.creative.tools.CreativeToolType;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

public final class NoCuboidSelection implements CuboidSelection {
    private final CreativePlayer player;

    private DisplayCuboid selectCuboid;

    public NoCuboidSelection(CreativePlayer player) {
        this(player, null);
    }

    public NoCuboidSelection(CreativePlayer player, @Nullable DisplayCuboid selectCuboid) {
        this.player = player;
        this.selectCuboid = selectCuboid;
        if (selectCuboid != null) {
            selectCuboid.withGlowColor(getHighlightColor());
        }
    }

    @Override
    public void tick() {
        if (player.isHoldingTool(CreativeToolType.SELECTION)) {
            tickWithTool();
        } else {
            tickWithoutTool();
        }
    }

    private void tickWithTool() {
        spawnCuboid();

        Vec selectedBlock = player.getSnipe().computeTarget().asVec();
        BoundingBox boundingBox = new BoundingBox(1, 1, 1, selectedBlock.apply(Vec.Operator.FLOOR));
        selectCuboid.withInterpolation(false).adjust(boundingBox);
    }

    private void spawnCuboid() {
        Vec selectedBlock = player.getSnipe().computeTarget().asVec();
        BoundingBox boundingBox = new BoundingBox(1, 1, 1, selectedBlock.apply(Vec.Operator.FLOOR));
        if (selectCuboid == null) {
            Color highlightColor = getHighlightColor();
            selectCuboid = DisplayCuboid.spawn(player.getInstance(), boundingBox)
                    .withGlowColor(highlightColor);
        }
    }

    private void tickWithoutTool() {
        if (selectCuboid == null) return;
        selectCuboid.remove();
        selectCuboid = null;
    }

    @Override
    public void remove() {
        if (selectCuboid != null) {
            selectCuboid.remove();
        }
    }

    public OneCuboidSelection selectFirst(Vec point) {
        spawnCuboid();
        return new OneCuboidSelection(player, point, selectCuboid);
    }

    public Color getHighlightColor() {
        return player.getCreativeStorage().getSelectionColors().getNoSelection();
    }

    @Override
    public SelectionCuboidStorage toStorage() {
        return new SelectionCuboidStorage(null, null);
    }
}
