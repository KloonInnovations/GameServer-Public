package io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder;

import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.tools.data.ItemBoundPattern;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cylinder.work.CylinderGenSettings;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.block.Block;

public class CylinderToolSettings implements ItemBoundPattern {
    private byte[] pattern;
    private double radius = CylinderGenSettings.DEFAULT_RADIUS;
    private double thickness = CylinderGenSettings.DEFAULT_THICKNESS;
    private boolean hollow = false;
    private boolean even = false;

    @Override
    public CreativePattern getPattern() {
        if (pattern == null) return new SingleBlockPattern(Block.STONE);
        return MinecraftInputStream.fromBytesSneaky(pattern, CreativePattern.CODEC);
    }

    @Override
    public boolean hasPattern() {
        return pattern != null;
    }

    @Override
    public void setPattern(CreativePattern pattern) {
        this.pattern = MinecraftOutputStream.toBytesSneaky(pattern, CreativePattern.CODEC);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getThickness() {
        return thickness;
    }

    public void setThickness(double thickness) {
        this.thickness = thickness;
    }

    public boolean isHollow() {
        return hollow;
    }

    public void setHollow(boolean hollow) {
        this.hollow = hollow;
    }

    public boolean isEven() {
        return even;
    }

    public void setEven(boolean even) {
        this.even = even;
    }

    public CylinderGenSettings createGenSettings(BlockVec target, MaskLookup mask) {
        return new CylinderGenSettings(
                target,
                getPattern(),
                mask,
                radius,
                thickness,
                hollow,
                !even
        );
    }
}
