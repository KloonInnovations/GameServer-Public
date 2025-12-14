package io.kloon.gameserver.modes.creative.tools.impl.shape.cube;

import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.tools.data.ItemBoundPattern;
import io.kloon.gameserver.modes.creative.tools.impl.shape.cube.work.CubeGenSettings;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;

public class CubeToolSettings implements ItemBoundPattern {
    private int width = 5;
    private int height = 5;
    private int depth = 5;
    private boolean cuboidButtons = false;

    private byte[] pattern;

    private boolean hollow;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Vec getDimensions() {
        return new Vec(width, height, depth);
    }

    public boolean isCube() {
        return width == height && height == depth;
    }

    public void setSize(int size) {
        this.width = size;
        this.height = size;
        this.depth = size;
    }

    @Override
    public boolean hasPattern() {
        return pattern != null;
    }

    @Override
    public CreativePattern getPattern() {
        if (pattern == null) return new SingleBlockPattern(Block.STONE);
        return MinecraftInputStream.fromBytesSneaky(pattern, CreativePattern.CODEC);
    }

    @Override
    public void setPattern(CreativePattern pattern) {
        this.pattern = MinecraftOutputStream.toBytesSneaky(pattern, CreativePattern.CODEC);
    }

    public boolean isHollow() {
        return hollow;
    }

    public void setHollow(boolean hollow) {
        this.hollow = hollow;
    }

    public boolean hasCuboidButtons() {
        return cuboidButtons;
    }

    public void setCuboidButtons(boolean cuboidButtons) {
        this.cuboidButtons = cuboidButtons;
    }

    public CubeGenSettings createGenSettings(BlockVec target, MaskLookup mask) {
        Vec dimensions = new Vec(width, height, depth);
        Vec offset = dimensions.mul(0.5).apply(Vec.Operator.FLOOR);
        Point bbStart = target.sub(offset);
        BoundingBox bb = new BoundingBox(width, height, depth, bbStart);
        return new CubeGenSettings(
                bb,
                getPattern(),
                mask,
                hollow
        );
    }
}
