package io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid;

import io.kloon.gameserver.minestom.io.MinecraftInputStream;
import io.kloon.gameserver.minestom.io.MinecraftOutputStream;
import io.kloon.gameserver.modes.creative.masks.lookup.MaskLookup;
import io.kloon.gameserver.modes.creative.patterns.CreativePattern;
import io.kloon.gameserver.modes.creative.patterns.impl.SingleBlockPattern;
import io.kloon.gameserver.modes.creative.tools.data.ItemBoundPattern;
import io.kloon.gameserver.modes.creative.tools.impl.shape.pyramid.work.PyramidGenSettings;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

public class PyramidToolSettings implements ItemBoundPattern {
    private int steps = PyramidGenSettings.DEFAULT_STEP;
    private int stepHeight = PyramidGenSettings.DEFAULT_STEP_HEIGHT;
    private int stepLength = PyramidGenSettings.DEFAULT_STEP_LENGTH;
    private byte[] pattern;
    private boolean hollow = false;
    private boolean upsideDown = false;

    @Override
    public boolean hasPattern() {
        return pattern != null;
    }

    @Override
    public @Nullable CreativePattern getPattern() {
        if (pattern == null) return new SingleBlockPattern(Block.SANDSTONE);
        return MinecraftInputStream.fromBytesSneaky(pattern, CreativePattern.CODEC);
    }

    @Override
    public void setPattern(CreativePattern pattern) {
        this.pattern = MinecraftOutputStream.toBytesSneaky(pattern, CreativePattern.CODEC);
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getStepHeight() {
        return stepHeight;
    }

    public void setStepHeight(int stepHeight) {
        this.stepHeight = stepHeight;
    }

    public int getStepLength() {
        return stepLength;
    }

    public void setStepLength(int stepLength) {
        this.stepLength = stepLength;
    }

    public boolean isHollow() {
        return hollow;
    }

    public void setHollow(boolean hollow) {
        this.hollow = hollow;
    }

    public boolean isUpsideDown() {
        return upsideDown;
    }

    public void setUpsideDown(boolean upsideDown) {
        this.upsideDown = upsideDown;
    }

    public PyramidGenSettings createGenSettings(BlockVec bottomCenter, MaskLookup mask) {
        return new PyramidGenSettings(
                bottomCenter,
                getPattern(),
                mask,
                steps,
                stepHeight,
                stepLength,
                hollow,
                upsideDown);
    }
}
