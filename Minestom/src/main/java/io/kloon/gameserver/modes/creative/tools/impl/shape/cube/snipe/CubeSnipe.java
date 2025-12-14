package io.kloon.gameserver.modes.creative.tools.impl.shape.cube.snipe;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.selection.rendering.DisplayCuboid;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.CubeTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.CubeToolSettings;
import io.kloon.gameserver.modes.creative.tools.snipe.ToolSnipe;
import io.kloon.gameserver.modes.creative.tools.snipe.quick.TargetBlockDisplay;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.SnipeVisibility;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;

public class CubeSnipe extends ToolSnipe<CubeToolSettings> {
    private final CubeTool tool;

    private TargetBlockDisplay targetDisplay;

    private DisplayCuboid cubeDisplay;

    public CubeSnipe(CreativePlayer player, CubeTool tool) {
        super(player);
        this.tool = tool;
    }

    @Override
    protected void handleTick(BlockVec target, CubeToolSettings settings) {
        SnipeVisibility vis = player.getSnipe().getVisibility();

        if (targetDisplay == null) {
            targetDisplay = new TargetBlockDisplay(this)
                    .withMaterial(vis.editMat(Material.BROWN_STAINED_GLASS))
                    .withGlowColor(vis.editGlow(new Color(255, 170, 0)));
            targetDisplay.setInstance(instance);
        }

        if (!vis.isVisible() && cubeDisplay != null) {
            cubeDisplay.remove();
            cubeDisplay = null;
        }

        if (vis.isVisible()) {
            Vec dimensions = new Vec(
                    settings.getWidth(),
                    settings.getHeight(),
                    settings.getDepth());
            Vec offset = dimensions.mul(0.5).apply(Vec.Operator.FLOOR);
            Vec bbStart = target.asVec().sub(offset);

            BoundingBox boundingBox = new BoundingBox(dimensions.x(), dimensions.y(), dimensions.z(), bbStart);
            if (cubeDisplay == null) {
                cubeDisplay = DisplayCuboid.spawn(instance, boundingBox);
            }
            cubeDisplay
                    .withMaterial(vis.editMat(Block.ORANGE_STAINED_GLASS))
                    .withGlowColor(vis.editGlow(new Color(247, 192, 84)))
                    .adjust(boundingBox);
        }
    }

    @Override
    protected void handleRemove() {
        if (targetDisplay != null) {
            targetDisplay.remove();
        }

        if (cubeDisplay != null) {
            cubeDisplay.remove();
        }
    }
}
