package io.kloon.gameserver.modes.creative.tools.impl.shape.sphere;

import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.tools.data.ItemBoundPattern;
import io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.snipe.SphereSnipeSettings;
import io.kloon.gameserver.modes.creative.tools.impl.shape.sphere.work.SphereGenSettings;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.block.Block;

public class SphereToolSettings implements ItemBoundPattern, SphereSnipeSettings {
    private int radius = SphereTool.DEFAULT_RADIUS;
    private byte[] pattern;
    private boolean hollow = false;
    private boolean centered = true;

    @Override
    public double getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public boolean hasPattern() {
        return pattern != null;
    }

    public CreativePattern getPattern() {
        if (pattern == null) return new SingleBlockPattern(Block.STONE);
        return MinecraftInputStream.fromBytesSneaky(pattern, CreativePattern.CODEC);
    }

    public void setPattern(CreativePattern pattern) {
        this.pattern = MinecraftOutputStream.toBytesSneaky(pattern, CreativePattern.CODEC);
    }

    public boolean isHollow() {
        return hollow;
    }

    public void setHollow(boolean hollow) {
        this.hollow = hollow;
    }

    public boolean isEven() {
        return !centered;
    }

    public void setCentered(boolean centered) {
        this.centered = centered;
    }

    public boolean isCentered() {
        return centered;
    }

    public SphereGenSettings createGenSettings(BlockVec targetPos, MaskLookup mask) {
        return new SphereGenSettings(
                targetPos,
                radius,
                getPattern(),
                hollow,
                centered,
                mask
        );
    }
}
