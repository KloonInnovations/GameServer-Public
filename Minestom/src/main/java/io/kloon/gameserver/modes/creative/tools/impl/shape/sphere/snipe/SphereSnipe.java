package io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.snipe;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.SphereTool;
import io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.SphereToolSettings;
import io.kloon.gameserver.modes.creative.tools.snipe.ToolSnipe;
import io.kloon.gameserver.modes.creative.tools.snipe.quick.SphereDisplay;
import io.kloon.gameserver.modes.creative.tools.snipe.quick.TargetBlockDisplay;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.SnipeVisibility;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.item.Material;

public class SphereSnipe extends ToolSnipe<SphereToolSettings> {
    private final SphereTool tool;

    private TargetBlockDisplay targetDisplay;
    private SphereDisplay sphereDisplay;

    private static final Material MATERIAL = Material.BLUE_STAINED_GLASS;
    private static final Color GLOW_COLOR = new Color(0, 0, 255);

    public SphereSnipe(CreativePlayer player, SphereTool tool) {
        super(player);
        this.tool = tool;
    }

    @Override
    protected void handleTick(BlockVec target, SphereToolSettings settings) {
        SnipeVisibility vis = player.getSnipe().getVisibility();

        if (targetDisplay == null) {
            targetDisplay = new TargetBlockDisplay(this)
                    .withMaterial(vis.editMat(MATERIAL))
                    .withGlowColor(vis.editGlow(GLOW_COLOR));
            targetDisplay.setInstance(instance);
        }

        if (!vis.isVisible() && sphereDisplay != null) {
            sphereDisplay.remove();;
            sphereDisplay = null;
        }

        if (vis.isVisible()) {
            if (sphereDisplay == null) {
                sphereDisplay = new SphereDisplay(instance);
            }

            double radius = settings.getRadius() + 0.5;
            boolean odd = settings.isCentered();
            boolean animate = tool.getPlayerBound(player).isAnimatePreview();

            sphereDisplay
                    .withMaterial(vis.editMat(MATERIAL))
                    .withGlowColor(vis.editGlow(GLOW_COLOR))
                    .update(target, radius, odd, animate);
        }
    }

    @Override
    protected void handleRemove() {
        if (targetDisplay != null) {
            targetDisplay.remove();
            targetDisplay = null;
        }

        if (sphereDisplay != null) {
            sphereDisplay.remove();
        }
    }
}
