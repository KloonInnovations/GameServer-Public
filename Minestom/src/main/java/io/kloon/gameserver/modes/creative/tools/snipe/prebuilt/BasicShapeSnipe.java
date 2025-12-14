package io.kloon.gameserver.modes.creative.tools.snipe.prebuilt;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.menu.preferences.common.radius.RadiusSettings;
import io.kloon.gameserver.modes.creative.menu.preferences.common.snipeshape.SnipeShapePref;
import io.kloon.gameserver.modes.creative.tools.CreativeTool;
import io.kloon.gameserver.modes.creative.tools.snipe.ToolSnipe;
import io.kloon.gameserver.modes.creative.tools.snipe.quick.QuickDisplay;
import io.kloon.gameserver.modes.creative.tools.snipe.quick.TargetBlockDisplay;
import io.kloon.gameserver.modes.creative.tools.snipe.settings.SnipeVisibility;
import io.kloon.gameserver.modes.creative.tools.snipe.shape.SnipeShape;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.item.Material;

public class BasicShapeSnipe<Settings extends RadiusSettings, Preferences extends SnipeShapePref> extends ToolSnipe<Settings> {
    private final CreativeTool<Settings, Preferences> tool;

    private TargetBlockDisplay targetDisplay;
    private QuickDisplay shapeDisplay;

    private SnipeShape snipeShape;

    private final Material material;
    private final Color glowColor;

    public BasicShapeSnipe(CreativePlayer player, CreativeTool<Settings, Preferences> tool, Material material, Color glowColor) {
        super(player);
        this.tool = tool;
        this.material = material;
        this.glowColor = glowColor;
    }

    @Override
    protected void handleTick(BlockVec target, Settings settings) {
        Preferences preferences = tool.getPlayerBound(player);
        SnipeVisibility visibility = player.getSnipe().getVisibility();

        Material material = this.material;
        Color glowColor = visibility == SnipeVisibility.GLOWING ? this.glowColor : null;
        if (visibility == SnipeVisibility.DESATURATED) {
            material = Material.WHITE_STAINED_GLASS;
        }

        if (targetDisplay == null) {
            targetDisplay = new TargetBlockDisplay(this).withMaterial(material);
            targetDisplay.setInstance(instance);
        }
        targetDisplay.withMaterial(material);
        targetDisplay.withGlowColor(glowColor);

        SnipeShape prefShape = visibility == SnipeVisibility.INVISIBLE
                ? SnipeShape.NONE
                : preferences.getSnipeShape();
        if (this.snipeShape != prefShape) {
            if (shapeDisplay != null) {
                shapeDisplay.remove();
                shapeDisplay = null;
            }
            this.snipeShape = prefShape;
        }

        if (shapeDisplay == null) {
            shapeDisplay = snipeShape.createQuickDisplay(instance);
        }

        shapeDisplay.withMaterial(material);
        shapeDisplay.withGlowColor(glowColor);

        double radius = settings.getRadius();
        shapeDisplay.update(target, radius, true, false);
    }

    @Override
    protected void handleRemove() {
        if (targetDisplay != null) {
            targetDisplay.remove();
            targetDisplay = null;
        }

        if (shapeDisplay != null) {
            shapeDisplay.remove();
        }
    }
}
