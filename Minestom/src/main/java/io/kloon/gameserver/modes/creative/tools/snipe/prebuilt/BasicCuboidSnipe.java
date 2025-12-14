package io.kloon.gameserver.modes.creative.tools.snipe.prebuilt;

import io.kloon.gameserver.Kgs;
import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.selection.rendering.DisplayCuboid;
import io.kloon.gameserver.modes.creative.tools.snipe.ToolSnipe;
import io.kloon.gameserver.modes.creative.tools.snipe.quick.TargetBlockDisplay;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.SnipeVisibility;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.item.Material;

import java.util.function.Function;

public class BasicCuboidSnipe<Settings> extends ToolSnipe<Settings> {
    private final Material material;
    private final Color glowColor;
    private final Function<Settings, BoundingBox> bbFunction;

    private TargetBlockDisplay targetDisplay;
    private DisplayCuboid cuboidDisplay;

    public BasicCuboidSnipe(CreativePlayer player, Material material, RGBLike glowColor, Function<Settings, BoundingBox> bbFunction) {
        super(player);
        this.material = material;
        this.glowColor = new Color(glowColor);
        this.bbFunction = bbFunction;
    }

    @Override
    protected void handleTick(BlockVec target, Settings settings) {
        SnipeVisibility vis = player.getSnipe().getVisibility();

        if (targetDisplay == null) {
            targetDisplay = new TargetBlockDisplay(this);
            targetDisplay.setInstance(instance);
        }
        targetDisplay
                .withMaterial(vis.editMat(material))
                .withGlowColor(vis.editGlow(glowColor));

        if (!vis.isVisible() && cuboidDisplay != null) {
            cuboidDisplay.remove();
            cuboidDisplay = null;
        }

        if (vis.isVisible()) {
            BoundingBox boundingBox = bbFunction.apply(settings);
            if (cuboidDisplay == null) {
                cuboidDisplay = DisplayCuboid.spawn(instance, boundingBox);
            }
            cuboidDisplay
                    .withMaterial(vis.editMat(material.block()))
                    .withGlowColor(vis.editGlow(glowColor))
                    .adjust(boundingBox);
        }
    }

    @Override
    protected void handleRemove() {
        if (targetDisplay != null) {
            targetDisplay.remove();
            targetDisplay = null;
        }

        if (cuboidDisplay != null) {
            cuboidDisplay.remove();
        }
    }
}
