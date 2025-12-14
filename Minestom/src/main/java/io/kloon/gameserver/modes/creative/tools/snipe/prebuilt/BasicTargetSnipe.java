package io.kloon.gameserver.modes.creative.tools.snipe.prebuilt;

import io.kloon.gameserver.modes.creative.CreativePlayer;
import io.kloon.gameserver.modes.creative.tools.snipe.ToolSnipe;
import io.kloon.gameserver.modes.creative.tools.snipe.quick.TargetBlockDisplay;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.item.Material;

public class BasicTargetSnipe extends ToolSnipe {
    private final Material material;
    private final Color glowColor;

    private TargetBlockDisplay targetDisplay;

    public BasicTargetSnipe(CreativePlayer player, Material material, Color glowColor) {
        super(player);
        this.material = material;
        this.glowColor = glowColor;
    }

    @Override
    protected void handleTick(BlockVec target, Object settings) {
        if (targetDisplay == null) {
            targetDisplay = new TargetBlockDisplay(this)
                    .withMaterial(material)
                    .withGlowColor(glowColor);
            targetDisplay.setInstance(instance);
        }
    }

    @Override
    protected void handleRemove() {
        if (targetDisplay != null) {
            targetDisplay.remove();
        }
    }
}
